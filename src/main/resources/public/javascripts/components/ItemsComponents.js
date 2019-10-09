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
        fetch(`/events/`, {method: "POST", body: formData})
        alert('Your event was created!');
        event.preventDefault();
    }

    render() {
        let style = {display: "none"};
        if (this.props.showForm) { style = {display: "block"}};
        return (
            <form onSubmit={this.handleSubmit} style={style}>
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
                <input id="endDate" name="endDate" type="date"/>
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
            <li>
                <EventStartTime event={this.props.event}/>
                <EventEndTime   event={this.props.event}/>
                <EventDescription event={this.props.event}/>
            </li>
        );
    }
}
class EventStartTime extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    handleFocus() {
        this.setState({startTime: this.props.event.startTime });
    }
    handleChange(event) {
        this.setState({ startTime: event.target.value });
    }

    async handleBlur() {
        const formData = new FormData();
        formData.append("startTime", this.state.startTime);
        await fetch(`/events/${this.props.event.id}`, { method: "PUT", body: formData });
        this.setState(null);
    }

    render() {
        return (
            <input
                type="text"
                name="startTime"
                autoComplete="off"
                value={this.state === null ? this.props.event.startTime : this.state.startTime}
                onFocus={() => { this.handleFocus(); }}
                onChange={event => { this.handleChange(event); }}
                onBlur={() => { this.handleBlur(); }}
            />
        );
    }
}
class EventEndTime extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    handleFocus() {
        this.setState({endTime: this.props.event.endTime });
    }
    handleChange(event) {
        this.setState({ endTime: event.target.value });
    }

    async handleBlur() {
        const formData = new FormData();
        formData.append("endTime", this.state.endTime);
        await fetch(`/events/${this.props.event.id}`, { method: "PUT", body: formData });
        this.setState(null);
    }

    render() {
        return (
            <input
                type="text"
                name="endTime"
                autoComplete="off"
                value={this.state === null ? this.props.event.endTime : this.state.endTime}
                onFocus={() => { this.handleFocus(); }}
                onChange={event => { this.handleChange(event); }}
                onBlur={() => { this.handleBlur(); }}
            />
        );
    }
}

class EventDescription extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    handleFocus() {
        this.setState({ description: this.props.event.description });
    }

    handleChange(event) {
        this.setState({ description: event.target.value });
    }

    async handleBlur() {
        const formData = new FormData();
        formData.append("description", this.state.description);
        await fetch(`/events/${this.props.event.id}`, { method: "PUT", body: formData });
        this.setState(null);
    }

    render() {
        return (
            <input
                type="text"
                name="description"
                autoComplete="off"
                value={this.state === null ? this.props.event.description : this.state.description}
                onFocus={() => { this.handleFocus(); }}
                onChange={event => { this.handleChange(event); }}
                onBlur={() => { this.handleBlur(); }}
            />
        );
    }
}