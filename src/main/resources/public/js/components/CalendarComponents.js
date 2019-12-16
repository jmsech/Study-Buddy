class NewEventButton extends React.Component {
    render() {
        let title = "New Event";
        if (this.props.showEventForm) {
            title = "Cancel";
        }
        return <button className="btn centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class NewEventForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {users: []};

        this.handleSubmit = this.handleSubmit.bind(this);
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

    async getDataFromServer() {
        this.setState({ users: await (await fetch("/users/", {method : "GET"})).json() });
    }

    handleSubmit(event) {
        this.props.flip();
        const formData = new FormData();
        formData.append("userId", this.props.userID);
        formData.append("title", event.target.title.value);
        // combine tim/date into the format yyyy-mm-dd 00:00
        formData.append("startTime", event.target.startDate.value + " " + event.target.startTime.value);
        formData.append("endTime", event.target.endDate.value + " " + event.target.endTime.value);
        formData.append("description", event.target.description.value);
        formData.append("location", event.target.location.value);
        // Build list of comma separated emails from the chips
        const chips = M.Chips.getInstance(document.getElementById("newEventChipsInviteList")).chipsData;
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
        event.preventDefault();
        // TODO: get default values to not be covered on the second time after you submit form
        fetch(`../${this.props.userID}/events`, {method: "POST", body: formData})
            .then(this.handleResponse);
        event.target.reset(); // clear the form entries
    }

    componentDidMount() {
        this.getDataFromServer();
        // Initialize materialize datepicker and timepicker
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
        var ampm = (hours >= 12 && hours < 24) ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? '0'+ minutes : minutes;
        return hours + ':' + minutes + ' ' + ampm;
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
        if (this.props.showEventForm) { style = {display: "block"} }
        let date = new Date();
        // Define default start date
        let defaultStartDate = date.getFullYear()+'-'+(date.getMonth()+1)+'-';
        if (date.getDate() < 10) {
            defaultStartDate = defaultStartDate + "0" + date.getDate();
        } else {
            defaultStartDate = defaultStartDate + date.getDate();
        }
        // Define default end date
        if (date.getHours() >= 23) {
            date.setDate(date.getDate() + 1);
        }
        let defaultEndDate = date.getFullYear()+'-'+(date.getMonth()+1)+'-';
        if (date.getDate() < 10) {
            defaultEndDate = defaultEndDate + "0" + date.getDate();
        } else {
            defaultEndDate = defaultEndDate + date.getDate();
        }
        return (
            <form id="eventform" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="title">Event name</label>
                    <input id="title" name="title" type="text" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="description">Event description</label>
                    <textarea id="description" name="description" className="materialize-textarea"/>
                </div>
                <div className="input-field">
                    <label htmlFor="location">Event location</label>
                    <input id="location" name="location" type="text"/>
                </div>
                <div className="chips chips-placeholder chips-autocomplete" id="newEventChipsInviteList">
                    <input id="newEventInviteList" name="newEventInviteList" className="custom-class"/>
                </div>
                <div className="input-field">
                    <label htmlFor="startDate" className="active">Start date</label>
                    <input id="startDate" type="text" className="datepicker" defaultValue={defaultStartDate} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="startTime" className="active">Start time</label>
                    <input id="startTime" name="startTime" type="text" className="timepicker" defaultValue={this.formatAMPM(date.getHours(), date.getMinutes())} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endDate" className="active">End date</label>
                    <input id="endDate" name="endDate" type="text" className="datepicker" defaultValue={defaultEndDate} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endTime" className="active">End time</label>
                    <input id="endTime" name="endTime" type="text" className="timepicker" defaultValue={this.formatAMPM(date.getHours()+1, date.getMinutes())} required/>
                </div>
                <button className="btn white-text">Save Event</button>
            </form>
        );
        }
}

class EventList extends React.Component {
    constructor(props) {
        super(props);
        this.state = { events: [] };
    }

    async getDataFromServer() {
        // This first line removes past events
        await fetch(`/${this.props.userID}/events`, {method: "PUT"});
        this.setState({ events: await (await fetch(`/${this.props.userID}/events`)).json() });
        window.setTimeout(() => { this.getDataFromServer(); }, 200);
    }

    componentDidMount() {
        this.getDataFromServer();
    }

    render() {
        return (
        <div>
            <ul>{this.state.events.map(event => <Event key={event.id} event={event} userID={this.props.userID}/>)}</ul>
        </div>
        );
    }
}


class Event extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showForm: false, showAttendees: false};
    }

    flipFormState() {
        this.setState({showForm: !this.state.showForm} );
    }

    flipAttendeesState() {
        this.setState({showAttendees: !this.state.showAttendees});
    }

    render() {
        if (this.props.event.isDeadline) {
            return (
                <li className="card hoverable red lighten-2" style={{height: "20%"}}>
                    <div className="card-content black-text">
                         <span className="rowC">
                             <EventTitle event={this.props.event}/><DeleteButton event={this.props.event} userID={this.props.userID} deadline={true}/>
                         </span>
                        <DeadlineDateTime event={this.props.event}/>
                    </div>
                </li>
            );
        } else {
            return (
                <li className="card hoverable teal lighten-2">
                    <div className="card-content black-text">
                        <span className="card-title">
                            <EventTitle event={this.props.event}/>
                        </span>
                        <EventDateTime event={this.props.event}/>
                        <EventDescription event={this.props.event}/>
                        <EventLocation event={this.props.event}/>
                        <ShowAttendeesButton flip={this.flipAttendeesState.bind(this)}
                                             showAttendees={this.state.showAttendees}/>
                        <EventInviteList event={this.props.event} showAttendees={this.state.showAttendees}/><br/>
                        <AddToGoogleCalendarButton event={this.props.event}/>
                    </div>
                    <div className="card-action right-align">
                        <div id="edit-delete">
                            <EditButton flip={this.flipFormState.bind(this)} showForm={this.state.showForm}/>
                            <DeleteButton event={this.props.event} userID={this.props.userID}/>
                        </div>
                        <EditEventForm event={this.props.event} userID={this.props.userID}
                                       showForm={this.state.showForm} flip={this.flipFormState.bind(this)}/>
                    </div>
                </li>
            );
        }
    }
}

class EventTitle extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    componentDidMount() {
        M.Tooltip.init(document.querySelectorAll('.tooltipped'), {
        });
    }

    render() {
        let display = {display: "none"}
        if (this.props.event.conflict) {
            display = {display: "inline"}
        }
        return (
            <h4><a className="tooltipped" data-position="bottom" data-tooltip="This event has a time conflict with at least one other event" style={display}><i style={display} className="small material-icons" >warning</i></a> {this.props.event.title}</h4>
        );
    }
}

class EventDescription extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        if (this.props.event.description !== "" && this.props.event.description !== null) {
            return (
                <p> <i className="tiny material-icons">description</i> {this.props.event.description}</p>
            );
        } else return null;
    }
}

class EventLocation extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        if (this.props.event.location !== "" && this.props.event.description !== null){
            return (
                <div>
                    <p><i className="tiny material-icons">location_on</i>{this.props.event.location}</p>
                </div>
            );
        } else return null;
    }
}

class EventInviteList extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        if (this.props.showAttendees) {
            return (
                <div>
                    <ul>{this.props.event.attendees.map(user => <li key={user.id}>{user.name}</li>)}</ul>
                </div>
            );
        }
        else return null;
    }
}

function titleCase(str) {
    return str.substr(0, 1).toUpperCase() + str.substr(1, str.length).toLowerCase();
}

function convertTo12HourFormat(hour, minute) {
    let ampm = "AM";
    if (hour >= 12) {
        hour -= 12;
        ampm = "PM";
    }
    if (hour === 0) {
        hour = 12;
    }
    return ("" + hour + ":" + (("0" + minute).slice(-2)) + " " +  ampm);
}

class EventDateTime extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <div id="EventDateTime">
                <p>
                    <i className="tiny material-icons">date_range</i>&nbsp;
                    {titleCase(this.props.event.startTime.dayOfWeek)},&nbsp;
                    {titleCase(this.props.event.startTime.month)} {this.props.event.startTime.dayOfMonth}:&nbsp;
                    {convertTo12HourFormat(this.props.event.startTime.hour, this.props.event.startTime.minute)} -&nbsp;
                    {convertTo12HourFormat(this.props.event.endTime.hour, this.props.event.endTime.minute)}
                </p>
            </div>
        );
    }
}

class DeadlineDateTime extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <div id="DeadlineDateTime">
                <p>
                    <i className="tiny material-icons">date_range</i>&nbsp;
                    {titleCase(this.props.event.startTime.dayOfWeek)},&nbsp;
                    {titleCase(this.props.event.startTime.month)} {this.props.event.startTime.dayOfMonth}:&nbsp;
                    {convertTo12HourFormat(this.props.event.startTime.hour, this.props.event.startTime.minute)}
                </p>
            </div>
        );
    }
}

class DeleteButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        const basePath = `../${this.props.userID}/events/`;
        const path = basePath.concat(this.props.event.id);
        let className = "btn"
        if (this.props.deadline) {
            className = "btn red lighten-2 right-align"
        }
        return (
            <button className={className} onClick={() => { fetch(path, {method: "DELETE"}) }}><i className="material-icons">delete</i></button>
        )
    }
}

class EditButton extends React.Component {
    render() {
        let title = "Edit";
        if (this.props.showForm) {
            title = "Cancel";
        }
        return <button className="btn" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

function convertToMonth(str) {
    let abv = str.substr(0, 1).toUpperCase() + str.substr(1, 2).toLowerCase();
    return "JanFebMarAprMayJunJulAugSepOctNovDec".indexOf(abv) / 3 + 1;
}

class ShowAttendeesButton extends React.Component {
    render() {
        let title = "Show Attendees";
        if (this.props.showAttendees) {
            title = "Hide Attendees";
        }
        return <a onClick={() => { this.props.flip() }}>{title}</a>;
    }
}

class AddToGoogleCalendarButton extends React.Component {
    createUrlParam(base, content) {
        if (content === null || content === "") {
            return null
        }
        return base + encodeURI(content);
    }

    createGoogleCalendarUrl() {
        const baseUrl = "https://www.google.com/calendar/render?action=TEMPLATE";
        const titleParam =  this.createUrlParam("&text=", this.props.event.title);
        const st = this.props.event.startTime;
        const startTimeString = new Date(st.year, (st.monthValue-1), st.dayOfMonth, st.hour, st.minute, st.second)
            .toISOString().replace(/-|:|\.\d\d\d/g,"");
        const et = this.props.event.endTime;
        const endTimeString = new Date(et.year, (et.monthValue-1), et.dayOfMonth, et.hour, et.minute, et.second)
            .toISOString().replace(/-|:|\.\d\d\d/g,"");
        const timeParam = "&dates=" + startTimeString + "/" + endTimeString;
        const descriptionParam = this.createUrlParam("&details=", this.props.event.description);
        const locationParam = this.createUrlParam("&location=", this.props.event.location);
        let url = baseUrl + titleParam + timeParam;
        if (descriptionParam !== null) {
            url = url + descriptionParam;
        }
        if (locationParam !== null) {
            url = url + locationParam;
        }
        open(url);
    }

    render() {
        return <a onClick={this.createGoogleCalendarUrl.bind(this)}>Add to Google Calendar</a>;
    }
}

class EditEventForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};

        this.handleSubmit = this.handleSubmit.bind(this);
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

    formatTime(time) {
        if (time.length < 8) {
            return "0" + time;
        }
        return time;
    }

    getInviteList() {
        let inviteList = "";
        for (let i = 0; i < this.props.event.attendees.length; i++) {
            const email = this.props.event.attendees[i].email;
            inviteList = inviteList + email + ", ";
        }
        return inviteList.substr(0, inviteList.length - 2);
    }

    handleSubmit(event) {
        this.props.flip();
        const formData = new FormData();
        formData.append("userId", this.props.userID);
        formData.append("title", event.target.title.value);
        // combine tim/date into the format yyyy-mm-dd 00:00
        formData.append("startTime", event.target.startDate.value + " " + this.formatTime(event.target.startTime.value));
        formData.append("endTime", event.target.endDate.value + " " + this.formatTime(event.target.endTime.value));
        formData.append("description", event.target.description.value);
        formData.append("location", event.target.location.value);
        formData.append("inviteList", event.target.editEventInviteList.value);
        event.preventDefault();
        // TODO: get default values to not be covered on the second time after you submit form
        fetch(`../${this.props.userID}/events/${this.props.event.id}`, {method: "PUT", body: formData})
            .then(this.handleResponse);
        event.target.reset(); // clear the form entries
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

    render() {
        let style = {display: "none"};
        if (this.props.showForm) { style = {display: "block"}}

        let startMonth = convertToMonth(this.props.event.startTime.month);
        let startDay = this.props.event.startTime.dayOfMonth;
        let endMonth = convertToMonth(this.props.event.endTime.month);
        let endDay = this.props.event.startTime.dayOfMonth;

        let startDate = this.props.event.startTime.year + '-' + startMonth + '-';
        if (startDay < 10) {
            startDate = startDate + "0" + startDay;
        } else {
            startDate = startDate + startDay;
        }

        let endDate = this.props.event.startTime.year + '-' + endMonth + '-';
        if (endDay < 10) {
            endDate = endDate + "0" + endDay;
        } else {
            endDate = endDate + endDay;
        }

        return (
            <form id="edit-event-form" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="title" className="active">Event name</label>
                    <input id="title" name="title" type="text" defaultValue = {this.props.event.title} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="description" className="active">Event description</label>
                    <textarea id="description" name="description" className="materialize-textarea" defaultValue={this.props.event.description}/>
                </div>
                <div className="input-field">
                    <label htmlFor="location" className="active">Event location</label>
                    <input id="location" name="location" type="text" defaultValue={this.props.event.location}/>
                </div>
                <div className="input-field">
                    <label htmlFor="editEventInviteList" className="active">Invite list</label>
                    <textarea id="editEventInviteList" name="editEventInviteList" className="materialize-textarea" defaultValue={this.getInviteList()}/>
                </div>
                <div className="input-field">
                    <label htmlFor="startDate" className="active">Start date</label>
                    <input id="startDate" type="text" className="datepicker" defaultValue = {startDate} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="startTime" className="active">Start time</label>
                    <input id="startTime" name="startTime" type="text" className="timepicker" defaultValue = {convertTo12HourFormat(this.props.event.startTime.hour, this.props.event.startTime.minute)} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endDate" className="active">End date</label>
                    <input id="endDate" name="endDate" type="text" className="datepicker" defaultValue = {endDate} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endTime" className="active">End time</label>
                    <input id="endTime" name="endTime" type="text" className="timepicker" defaultValue = {convertTo12HourFormat(this.props.event.endTime.hour, this.props.event.endTime.minute)} required/>
                </div>
                <button className="btn white-text">Save Event</button>
            </form>
        );
    }
}