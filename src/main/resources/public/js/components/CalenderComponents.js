class NewEventButton extends React.Component {
    render() {
        let title = "New Event";
        if (this.props.showForm) { title = "Cancel" };
        return <button className={this.props.className} onClick={() => { this.props.flip() }}>{title}</button>;
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

    handleSubmit(event) {
        this.props.flip();
        const formData = new FormData();
        formData.append("userID", this.props.userID)
        formData.append("title", event.target.title.value)
        // combine tim/date into the format yyyy-mm-ddT00:00
        formData.append("startTime", event.target.startDate.value + " " + event.target.startTime.value)
        formData.append("endTime", event.target.endDate.value + " " + event.target.endTime.value)
        formData.append("description", event.target.description.value)
        event.target.reset(); // clear the form entries
        fetch(`${this.props.userID}/events`, {method: "POST", body: formData})
        event.preventDefault();
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
        return (
            <form id="eventform" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="title">Event name</label>
                    <input id="title" name="title" type="text" />
                </div>
                <br/>
                <div className="input-field">
                    <label htmlFor="description">Event description</label>
                    <input id="description" name="description" type="text" />
                </div>
                <br/>
                <div className="input-field">
                    <label htmlFor="startDate">Start date</label>
                    <input id="startDate" type="text" className="datepicker"/>
                </div>
                <br/>
                <div className="input-field">
                    <label htmlFor="startTime">Start time</label>
                    <input id="startTime" name="startTime" type="text" className="timepicker" />
                </div>
                <br/>
                <div className="input-field">
                    <label htmlFor="endDate">End date</label>
                    <input id="endDate" name="endDate" type="text" className="datepicker" />
                </div>
                <br/>
                <div className="input-field">
                    <label htmlFor="endTime">End time</label>
                    <input id="endTime" name="endTime" type="text" className="timepicker" />
                </div>
                <br/>
                <button className="btn black-text">Save Event</button>
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
        return <div>
            <h3>Here are your events:</h3>
            <ul>{this.state.events.map(event => <Event key={event.id} event={event}/>)}</ul>
        </div>;
    }
}

class Event extends React.Component {
    render() {
        return (
            <li className="card hoverable teal lighten-2">
                <div className="card-content black-text">
                    <span className="card-title"> <EventTitle event={this.props.event}/></span>
                    <EventDescription event={this.props.event}/>
                    <EventDateTime event={this.props.event}/>
                </div>
                <div className="card-action right-align">
                    <DeleteButton event={this.props.event}/>
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
            <p>{this.props.event.description}</p>
        );
    }
}

function titleCase(str) {
    return str.substr(0, 1).toUpperCase() + str.substr(1, str.length).toLowerCase();
}

function convertTo12HourFormat(hour, minute) {
    let ampm = "AM";
    if (hour > 12) {
        hour -= 12;
        ampm = "PM";
    } else if (hour == 0) {
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
        const basePath = "/events/";
        const path = basePath.concat(this.props.event.id);
        return (
            <button className="btn" onClick={() => { fetch(path, {method: "DELETE"}) }}><i className="material-icons">delete</i></button>
        )
    }
}