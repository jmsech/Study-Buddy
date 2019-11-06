/*class Recommendation extends React.Component {
    constructor(props) {
        super(props);
        this.state = { recs: [] };
    }

    async getRec() {
        this.setState({recs: [await (await fetch(`/${this.props.userID}/recs`)).json()]});
    }

    clearRecs() {
        this.setState({recs: []});
    }

    render() {
        return (
            <div>
                <h3>Recommendations</h3>
                <NewRecButton className="new-event-button btn white-text" flip={this.props.flip} showForm={this.props.showForm} />
                <NewRecForm userID={this.state.userID} showForm={this.props.showForm} flip={this.props.flip}/>
                <RecList userID={this.props.userID} recs={this.state.recs} clearRecs={this.clearRecs.bind(this)}/>
            </div>
        )
    }
}*/

class NewRecButton extends React.Component {
    render() {
        let title = "Generate a Recommendation";
        if (this.props.showRecForm) {
            title = "Cancel";
        }
        //return <button className="btn cyan darken-3 centralized-button" onClick={() => { this.props.getRec() }}>{title}</button>;
        return <button className="btn cyan darken-3 centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class NewRecForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(rec) {
        this.setState({value: rec.target.value});
    }

    async handleResponse(response) {
        const msg = await response.json();
        if (msg === "RecPeriodError") {
            alert("Invalid recommendation period (start has to be before end)");
        }
        return response;
    }

    handleSubmit(rec) {
        this.props.flip();
        const formData = new FormData();
        formData.append("userID", this.props.userID);
        formData.append("user1", rec.target.user1.value);
        formData.append("user2", rec.target.user2.value);
        formData.append("user3", rec.target.user3.value);
        formData.append("user4", rec.target.user4.value);
        formData.append("user5", rec.target.user5.value);
        formData.append("startTime", rec.target.startDate.value + " " + rec.target.startTime.value);
        formData.append("endTime", rec.target.endDate.value + " " + rec.target.endTime.value);
        formData.append("sessionLength", rec.target.sessionLength.value);

        rec.preventDefault();
        fetch(`../${this.props.userID}/recs`, {method: "POST", body: formData})
            .then(this.handleResponse);
        rec.target.reset(); // clear the form entries
    }

    componentDidMount() {
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

    formatAMPM(hours, minutes) {
        var ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours
        minutes = minutes < 10 ? '0'+ minutes : minutes;
        var strTime = hours + ':' + minutes + ' ' + ampm;
        return strTime;
    }

    render() {
        let style = {display: "none"};
        if (this.props.showRecForm) { style = {display: "block"}};
        let today = new Date();
        let date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();
        return (
            <form id="eventform" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="user1">Study Buddy #1 email</label>
                    <input id="user1" name="user1" type="email" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="user2">Study Buddy #2 email</label>
                    <input id="user2" name="user2" type="email"/>
                </div>
                <div className="input-field">
                    <label htmlFor="user3">Study Buddy #3 email</label>
                    <input id="user3" name="user3" type="email"/>
                </div>
                <div className="input-field">
                    <label htmlFor="user4">Study Buddy #4 email</label>
                    <input id="user4" name="user4" type="email"/>
                </div>
                <div className="input-field">
                    <label htmlFor="user5">Study Buddy #5 email</label>
                    <input id="user5" name="user5" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="startDate" className="active">Recommend no earlier than this day</label>
                    <input id="startDate" type="text" className="datepicker" defaultValue = {date} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="startTime" className="active">Recommend no earlier than this time</label>
                    <input id="startTime" name="startTime" type="text" className="timepicker" defaultValue = {this.formatAMPM(today.getHours(), today.getMinutes())} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endDate" className="active">Recommend no later than this day</label>
                    <input id="endDate" name="endDate" type="text" className="datepicker" defaultValue = {date} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endTime" className="active">Recommend no later than this time</label>
                    <input id="endTime" name="endTime" type="text" className="timepicker" defaultValue = {this.formatAMPM(today.getHours()+1, today.getMinutes())} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="sessionLength" className="active">Study for this long (in hours) </label>
                    <input id="sessionLength" name="sessionLength" type="number"  defaultValue ="1" min="0.5" max="6" required/>
                </div>
                <button className="btn white-text">Get Recommendation!</button>
            </form>
        );
    }
}

class Rec extends React.Component {

    render() {
        return (
            <li className="card hoverable cyan darken-2">
                <div className="card-content black-text">
                    <span className="card-title"> <EventTitle event={this.props.event}/></span>
                    <EventDescription event={this.props.event}/>
                    <EventDateTime event={this.props.event}/>
                </div>
                <div id="recommendation-actions" className="card-action">
                    <RecAcceptButton event={this.props.event} userID={this.props.userID} clearRecs={this.props.clearRecs}/>
                    <RecDeclineButton clearRecs={this.props.clearRecs}/>
                </div>
            </li>
        );
    }
}

class RecList extends React.Component {
    constructor(props) {
        super(props);
        this.state = { recs: [] };
    }

    clearRecs() {
        this.setState({recs: []});
    }

    render() {
        return (
            <div>
                <ul>{this.props.recs.map(rec => <Rec key={rec.id} event={rec} userID={this.props.userID} clearRecs={this.props.clearRecs}/>)}</ul>
            </div>
        );
    }
}

class RecAcceptButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    formatAMPM(hours, minutes) {
        var ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours
        minutes = minutes < 10 ? '0'+ minutes : minutes;
        var strTime = hours + ':' + minutes + ' ' + ampm;
        return strTime;
    }

    handleClick() {
        const event = this.props.event;
        const formData = new FormData();
        formData.append("userID", this.props.userID);
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
        this.props.clearRecs();
        fetch(`../${this.props.userID}/events`, {method: "POST", body: formData});
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
            <button className="btn cyan darken-3" onClick={() => {this.props.clearRecs()}}>Decline</button>
        )
    }
}