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
        formData.append("title", event.target.title.value)
        // combine tim/date into the format yyyy-mm-ddT00:00
        formData.append("startTime", event.target.startDate.value + "T" + event.target.startTime.value)
        formData.append("endTime", event.target.endDate.value + "T" + event.target.endTime.value)
        formData.append("description", event.target.description.value)
        event.target.reset(); // clear the form entries
        fetch("/events", {method: "POST", body: formData})
        //alert("Your event was created!");
        event.preventDefault();
    }

    render() {
        let style = {display: "none"};
        if (this.props.showForm) { style = {display: "block"}};
        return (
            <form id="eventform" onSubmit={this.handleSubmit} style={style}>
                <label htmlFor="title">Event name: </label>
                <input id="title" name="title" type="text" />
                <br/>
                <label htmlFor="description">Event description: </label>
                <input id="description" name="description" type="text" />
                <br/>
                <label htmlFor="startDate">Start date: </label>
                <input id="startDate" name="startDate" type="date"/>
                <br/>
                <label htmlFor="startTime">Start time: </label>
                <input id="startTime" name="startTime" type="time" />
                <br/>
                <label htmlFor="endDate">End date: </label>
                <input id="endDate" name="endDate" type="date" />
                <br/>
                <label htmlFor="endTime">End time: </label>
                <input id="endTime" name="endTime" type="time" />
                <br/>
                <button>Save Event</button>
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
        this.setState({ events: await (await fetch("/events")).json() });
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
            <li className="event-card">
                <EventTitle event={this.props.event}/>
                <EventDescription event={this.props.event}/>
                <EventDateTime event={this.props.event}/>
                <DeleteButton event={this.props.event}/>
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
            <h3>{this.props.event.title}</h3>
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
                    {this.props.event.startTime.hour}:{("0" + this.props.event.startTime.minute).slice(-2)} -&nbsp;
                    {this.props.event.endTime.hour}:{("0" + this.props.event.endTime.minute).slice(-2)}
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
            <button onClick={() => { fetch(path, {method: "DELETE"}) }}>Delete event</button>
        )
    }
}