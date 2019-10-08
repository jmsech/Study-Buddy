class PlusButton extends React.Component {
    handleClick() {
        fetch("/events", { method: "POST" });
    }

    render() {
        return <button className={this.props.className} onClick={() => { this.handleClick(); }}>+</button>;
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
        return <ul>{this.state.events.map(event => <Event key={event.id} event={event}/>)}</ul>;
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