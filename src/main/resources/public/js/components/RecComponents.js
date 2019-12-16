class Recommendations extends React.Component {
    constructor(props) {
        super(props);
        this.state = { recs: [] };
    }

    clearRec(id) {
        let recToRemove;
        for (let i = 0; i < this.state.recs.length; i++) {
            if (this.state.recs[i].id === id) {
                recToRemove = this.state.recs[i];
            }
        }
        const filteredArray = this.state.recs.filter(function(e) { return e !== recToRemove });
        this.setState({recs: filteredArray});
    }

    clearRecs() {
        this.setState({recs: []});
    }

    setRecs(recs) {
        this.setState({recs: recs});
    }

    render() {
        return (
            <div>
                <NewRecButton className="new-event-button btn white-text" flip={this.props.flipRec} showRecForm={this.props.showRecForm} />
                <NewRecForm userID={this.props.userID} showRecForm={this.props.showRecForm} flip={this.props.flipRec} setRecs={this.setRecs.bind(this)}/>
                <RecList userID={this.props.userID} recs={this.state.recs} clearRecs={this.clearRecs.bind(this)} clearRec={this.clearRec.bind(this)}/>
            </div>
        )
    }
}

class NewRecButton extends React.Component {
    render() {
        let title = "Time Recommendation";
        if (this.props.showRecForm) {
            title = "Cancel";
        }
        return <button className="btn cyan darken-3 centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class NewRecForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {users: []};

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    async handleResponse(response) {
        const data = await response.json();
        if (data === "RecPeriodError") {
            alert("Invalid recommendation period (start has to be before end)");
        } else if (data ==="NoRecsToDisplay") {
            alert("There are no times in the specified time period where everyone is available")
        } else if (data === "InviteListError") {
            alert("Invalid invite list (should be list of existing users");
        } else {
            this.props.setRecs(data);
        }
        return response;
    }

    async handleSubmit(rec) {
        rec.preventDefault();
        this.props.flip();
        const formData = new FormData();
        formData.append("userId", this.props.userID);
        // Build list of comma separated emails from the chips
        const chips = M.Chips.getInstance(document.getElementById("recChipsInviteList")).chipsData;
        let inviteList = "";
        for (let i = 0; i < chips.length; i++) {
            const data = chips[i].tag;
            const startIndex = data.indexOf("(") + 1;
            const endIndex = data.indexOf(")");
            const length = endIndex - startIndex;
            const email = data.substr(startIndex, length);
            inviteList = inviteList.concat(email);
            if (i < chips.length - 1) { // If not the last email
                inviteList = inviteList.concat(", ");
            }
        }
        formData.append("inviteList", inviteList);
        formData.append("startTime", rec.target.startDate.value + " " + rec.target.startTime.value);
        formData.append("endTime", rec.target.endDate.value + " " + rec.target.endTime.value);
        formData.append("sessionLength", rec.target.sessionLength.value);

        rec.preventDefault();
        fetch(`../${this.props.userID}/recs`, {method: "POST", body: formData})
            .then(this.handleResponse);
        rec.target.reset(); // clear the form entries
    }

    async getDataFromServer() {
        this.setState({ users: await (await fetch("/users/", {method : "GET"})).json() });
    }

    componentDidMount() {
        this.getDataFromServer();
        M.Datepicker.init(document.querySelectorAll('.datepicker'), {
            autoClose: true,
            showClearBtn: true,
            format: "yyyy-mm-dd",
            minDate: (new Date())
        });
        M.Timepicker.init(document.querySelectorAll('.timepicker'), {
            showClearBtn: true
        });
    }

    formatTime(x) {
        let hours = x.getHours();
        let ampm = (hours >= 12 && hours < 24) ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours;
        let minutes = x.getMinutes();
        minutes = minutes < 10 ? '0'+ minutes : minutes;
        return hours + ":" + minutes + " " + ampm;
    }

    formatDay(x) {
        let date = x.getFullYear()+'-'+(x.getMonth()+1)+'-';
        date = (x.getDate() < 10) ? date + "0" + x.getDate() : date + x.getDate();
        return date;
    }

    render() {
        // Create autocomplete list of users
        let users = {};
        for (let i = 0; i < this.state.users.length; i++) {
            const user = this.state.users[i];
            const name = user.name;
            const email = user.email;
            const string = name.concat(" (", email, ")");
            Object.assign(users, {[string]: null});
        }

        const chipsAutocompleteOptions = {data: users, limit: 20};
        const options = {placeholder: "Invite List", autocompleteOptions: chipsAutocompleteOptions};
        // Initialize materialize autocomplete
        M.Chips.init(document.querySelectorAll('.chips'), options);

        let style = {display: "none"};
        if (this.props.showRecForm) { style = {display: "block"} }
        let date = new Date();
        // Define default start date
        let x = date.getTime();
        let y = x + 6*60*60*1000;
        let X = new Date(x);
        let Y = new Date(y);
        let timeX = this.formatTime(X);
        let timeY = this.formatTime(Y);
        let dayX = this.formatDay(X);
        let dayY = this.formatDay(Y);

        return (
            <form id="eventform" onSubmit={this.handleSubmit} style={style}>
                <div className="chips chips-placeholder chips-autocomplete" id="recChipsInviteList">
                    <input id="recInviteList" name="recInviteList" className="custom-class"/>
                </div>
                <div className="input-field">
                    <label htmlFor="startDate" className="active">Recommend no earlier than this day</label>
                    <input id="startDate" type="text" className="datepicker" defaultValue = {dayX} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="startTime" className="active">Recommend no earlier than this time</label>
                    <input id="startTime" name="startTime" type="text" className="timepicker" defaultValue = {timeX} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endDate" className="active">Recommend no later than this day</label>
                    <input id="endDate" name="endDate" type="text" className="datepicker" defaultValue = {dayY} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endTime" className="active">Recommend no later than this time</label>
                    <input id="endTime" name="endTime" type="text" className="timepicker" defaultValue = {timeY} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="sessionLength" className="active">Study for this long (in hours) </label>
                    <input id="sessionLength" name="sessionLength" type="number"  defaultValue ="1" min="1" max="6" required/> {/* These parameters should be put elsewhere */}
                </div>
                <button className="btn white-text">Get Recommendation!</button>
            </form>
        );
    }
}

class Rec extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showAttendees: false};
    }

    flipAttendeesState() {
        this.setState({showAttendees: !this.state.showAttendees});
    }

    render() {
        return (
            <li className="card hoverable cyan darken-2">
                <div className="card-content black-text">
                    <span className="card-title"> <EventTitle event={this.props.event}/></span>
                    <EventDateTime event={this.props.event}/>
                    <EventDescription event={this.props.event}/>
                    <EventLocation event={this.props.event}/>
                    <ShowAttendeesButton flip={this.flipAttendeesState.bind(this)} showAttendees={this.state.showAttendees}/>
                    <EventInviteList event={this.props.event} showAttendees={this.state.showAttendees}/>
                </div>
                <div id="recommendation-actions" className="card-action">
                    <RecAcceptButton event={this.props.event} userID={this.props.userID} clearRecs={this.props.clearRecs}/>
                    <RecDeclineButton clearRec={this.props.clearRec} event={this.props.event}/>
                </div>
            </li>
        );
    }
}

class RecList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {expanded: false, numRecs: 5};
    }

    expandCollapse() {
        if (this.state.expanded === true) {
            this.setState({expanded: false, numRecs: 5})
        } else if (this.state.numRecs + 5 < this.props.recs.length) {
            this.setState({numRecs: this.state.numRecs + 5})
        } else if (this.state.numRecs + 5 === this.props.recs.length) {
            this.setState({expanded: true, numRecs: this.state.numRecs + 5})
        } else {
            this.setState({expanded: true, numRecs: this.props.recs.length})
        }
    }

    render() {
        if (this.props.recs.length <= 5) {
            //display all of recs (less than 5 total so no Show More button)
            return (
                <div>
                    <ul>{this.props.recs.map(rec => <Rec key={rec.id} event={rec} userID={this.props.userID} clearRecs={this.props.clearRecs} clearRec={this.props.clearRec}/>)}</ul>
                </div>
            );
        } else if (this.state.expanded === false) {
            return (
                <div>
                    <ul>{this.props.recs.slice(0, this.state.numRecs).map(rec => <Rec key={rec.id} event={rec} userID={this.props.userID}
                                                                     clearRecs={this.props.clearRecs}
                                                                     clearRec={this.props.clearRec}/>)}</ul>
                    <button className="btn white-text" onClick = {() => this.expandCollapse()}>See More Recommendations</button>
                </div>
            );
        } else {
            return (
                <div>
                    <ul>{this.props.recs.map(rec => <Rec key={rec.id} event={rec} userID={this.props.userID}
                                                                     clearRecs={this.props.clearRecs}
                                                                     clearRec={this.props.clearRec}/>)}</ul>
                    <button className="btn white-text" onClick = {() => this.expandCollapse()}>Collapse Recommendations</button>
                </div>
            );
        }
    }
}

class RecAcceptButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    formatAMPM(hours, minutes) {
        const ampm = (hours >= 12 && hours < 24) ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? '0'+ minutes : minutes;
        return hours + ':' + minutes + ' ' + ampm;
    }

    async handleResponse(response) {
        const msg = await response.json();
        if (msg === "EventPeriodError") {
            alert("Invalid event period (start has to be before end)");
        } else if (msg === "InviteListError") {
            alert("Invalid invite list (should be list of existing users");
        }
        return response;
    }

    handleClick() {
        const event = this.props.event;
        const formData = new FormData();
        formData.append("userId", this.props.userID);
        formData.append("title", event.title);
        // Format startTime
        let startTime = String(event.startTime.year).concat("-").concat(event.startTime.monthValue).concat("-");
        if (event.startTime.dayOfMonth < 10) {
            startTime = startTime.concat("0").concat(event.startTime.dayOfMonth);
        } else {
            startTime = startTime.concat(event.startTime.dayOfMonth);
        }
        startTime = startTime.concat(" ").concat(this.formatAMPM(event.startTime.hour, event.startTime.minute));
        // Format endTime
        let endTime = String(event.startTime.year).concat("-").concat(event.endTime.monthValue).concat("-");
        if (event.endTime.dayOfMonth < 10) {
            endTime = endTime.concat("0").concat(event.endTime.dayOfMonth);
        } else {
            endTime = endTime.concat(event.endTime.dayOfMonth);
        }
        endTime = endTime.concat(" ").concat(this.formatAMPM(event.endTime.hour, event.endTime.minute));
        formData.append("startTime", startTime);
        formData.append("endTime", endTime);
        formData.append("description", event.description);
        // Get list of attendees emails
        let attendeeList = "";
        for (let i = 0; i < event.attendees.length; i++) {
            attendeeList = attendeeList + event.attendees[i].email + ", ";
        }
        attendeeList = attendeeList.substr(0, attendeeList.length - 2);
        formData.append("inviteList", attendeeList);
        this.props.clearRecs();
        fetch(`../${this.props.userID}/events`, {method: "POST", body: formData})
            .then(this.handleResponse);
    }

    render() {
        return (
            <button className="btn cyan darken-3" onClick={() => {this.handleClick()}}>Accept</button>
        )
    }
}

class RecDeclineButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <button className="btn cyan darken-3" onClick={() => {this.props.clearRec(this.props.event.id)}}>Decline</button>
        )
    }
}