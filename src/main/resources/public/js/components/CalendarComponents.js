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
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    async handleResponse(response) {
        const msg = await response.json();
        if (msg === "EventPeriodError") {
            alert("Invalid event period (start has to be before end)");
        } else if (msg === "InviteListError") {
            alert("Invalid invite list (should be comma separated list of existing user's emails");
        }
        return response;
    }

    handleSubmit(event) {
        this.props.flip();
        const formData = new FormData();
        formData.append("userID", this.props.userID);
        formData.append("title", event.target.title.value);
        // combine tim/date into the format yyyy-mm-dd 00:00
        formData.append("startTime", event.target.startDate.value + " " + event.target.startTime.value);
        formData.append("endTime", event.target.endDate.value + " " + event.target.endTime.value);
        formData.append("description", event.target.description.value);
        formData.append("location", event.target.location.value);
        formData.append("inviteList", event.target.inviteList.value);
        event.preventDefault();
        // TODO: get default values to not be covered on the second time after you submit form
        fetch(`../${this.props.userID}/events`, {method: "POST", body: formData})
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

    formatAMPM(hours, minutes) {
        var ampm = (hours >= 12 && hours < 24) ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours;
        minutes = minutes < 10 ? '0'+ minutes : minutes;
        return hours + ':' + minutes + ' ' + ampm;
    }

    render() {
        let style = {display: "none"};
        if (this.props.showEventForm) { style = {display: "block"}};
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
                    <input id="description" name="description" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="location">Event location</label>
                    <input id="location" name="location" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="inviteList">Invite list (insert comma-separated emails)</label>
                    <input id="inviteList" name="inviteList" type="text"/>
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
        this.state = {showForm: false};
    }

    flipFormState() {
        this.setState({showForm: !this.state.showForm});
    }

    render() {
        return (
            <li className="card hoverable teal lighten-2">
                <div className="card-content black-text">
                    <span className="card-title">
                        <EventTitle event={this.props.event}/>
                    </span>
                    <EventDescription event={this.props.event}/>
                    <EventLocation event={this.props.event}/>
                    <EventInviteList event={this.props.event}/>
                    <EventDateTime event={this.props.event}/>
                </div>
                <div className="card-action right-align">
                    <div id="edit-delete">
                        <EditButton flip={this.flipFormState.bind(this)}/>
                        <DeleteButton event={this.props.event} userID = {this.props.userID}/>
                    </div>
                    <EditEventForm event={this.props.event} userID={this.props.userID} showForm={this.state.showForm} flip={this.flipFormState.bind(this)}/>
                </div>
            </li>
        );
    }
}

class EventTitle extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <h4>{this.props.event.title}</h4>
        );
    }
}

class EventDescription extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <p> <i className="tiny material-icons">description</i>{this.props.event.description}</p>
        );
    }
}

class EventLocation extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <div>
                <p><i className="tiny material-icons">location_on</i>{this.props.event.location}</p>
            </div>
        );
    }
}

class EventInviteList extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <p>"Invite list here"</p>
        )
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
                    <i className="tiny material-icons">date_range</i>
                    {titleCase(this.props.event.startTime.dayOfWeek)},&nbsp;
                    {titleCase(this.props.event.startTime.month)} {this.props.event.startTime.dayOfMonth}:&nbsp;
                    {convertTo12HourFormat(this.props.event.startTime.hour, this.props.event.startTime.minute)} -&nbsp;
                    {convertTo12HourFormat(this.props.event.endTime.hour, this.props.event.endTime.minute)}
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
        return (
            <button className="btn" onClick={() => { fetch(path, {method: "DELETE"}) }}><i className="material-icons">delete</i></button>
        )
    }
}

class EditButton extends React.Component {
    /*constructor(props) {
        super(props);
        //this.state = null;
    }*/

    render() {
        //const basePath = `../${this.props.userID}/events/`;
        //const path = basePath.concat(this.props.event.id);
        return (
            <button className="btn" onClick={() => { this.props.flip() }}>Edit</button>
        )
    }
}

function convertToMonth(str) {
    let abv = str.substr(0, 1).toUpperCase() + str.substr(1, 2).toLowerCase();
    return "JanFebMarAprMayJunJulAugSepOctNovDec".indexOf(abv) / 3 + 1;
}

class EditEventForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    async handleResponse(response) {
        const msg = await response.json();
        if (msg === "EventPeriodError") {
            alert("Invalid event period (start has to be before end)");
        }
        return response;
    }

    formatTime(time) {
        if (time.length < 8) {
            return "0" + time;
        }
        return time;
    }

    handleSubmit(event) {
        this.props.flip();
        const formData = new FormData();
        formData.append("userID", this.props.userID);
        formData.append("title", event.target.title.value);
        // combine tim/date into the format yyyy-mm-dd 00:00
        formData.append("startTime", event.target.startDate.value + " " + this.formatTime(event.target.startTime.value));
        formData.append("endTime", event.target.endDate.value + " " + this.formatTime(event.target.endTime.value));
        // TODO: add invite list (including the event creator
        formData.append("description", event.target.description.value);
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
        if (this.props.showForm) { style = {display: "block"}};

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
            <form id="eventform" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="title" className="active">Event name</label>
                    <input id="title" name="title" type="text" defaultValue = {this.props.event.title}required/>
                </div>
                <div className="input-field">
                    <label htmlFor="description" className="active">Event description</label>
                    <input id="description" name="description" type="text" defaultValue={this.props.event.description}/>
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