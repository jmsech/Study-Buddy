class NewRecButton extends React.Component {
    render() {
        let title = "Generate a Recommendation";
        return <button className={this.props.className} onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class RecList extends React.Component {
    constructor(props) {
        super(props);
        this.state = { user1events:[], user2events: [], recs: [] };
    }

    async getDataFromServer() {
        //hardcoded for now
        this.setState({ user1events: await (await fetch(`/$1/events`)).json() });
        this.setState({ user2events: await (await fetch(`/$2/events`)).json() });
        //now get the union of the times they're unavailable
        //then get the complement of that to find available times
        //subtract out sleeping times (11pm - 8am?)

        window.setTimeout(() => { this.getDataFromServer(); }, 200);
    }

    componentDidMount() {
        this.getDataFromServer();
    }

    render() {
        return <div>
            <h3>Here are your recommendations:</h3>
            <ul>{this.state.recs.map(event => <Event key={event.id} event={event} userID = {this.props.userID}/>)}</ul>
        </div>;
    }
}

