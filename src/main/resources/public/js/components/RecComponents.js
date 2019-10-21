class NewRecButton extends React.Component {
    render() {
        let title = "Generate a Recommendation";
        return <button className={this.props.className} onClick={() => { this.props.getRec() }}>{title}</button>;
    }
}

class Rec extends React.Component {
    render() {
        return (
            <li className="card hoverable teal lighten-2"> /*  */
                <div className="card-content black-text">
                    <span className="card-title"> <EventTitle event={this.props.event}/></span>
                    <EventDescription event={this.props.event}/>
                    <EventDateTime event={this.props.event}/>
                </div>
                <div className="card-action right-align">
                    <DeleteButton event={this.props.event} userID = {this.props.userID}/>
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

    // async getDataFromServer() {
    //     // let jason = {
    //     //     "id": "5",
    //     //     "title": "reco",
    //     //     "startTime": "1571760000000",
    //     //     "endTime": "1571760000001",
    //     //     "description": "please work",
    //     //     "hosts": "null",
    //     //     "userID": "5"
    //     // };
    //
    //     //hardcoded for now
    //     this.setState({ recs: await (await fetch(`/${this.props.userID}/events`)).json() });
    //     // this.setState({ user2events: await (await fetch(`/$2/events`)).json() });
    //     //now get the union of the times they're unavailable
    //     //then get the complement of that to find available times
    //     //subtract out sleeping times (11pm - 8am?)
    //
    //     //window.setTimeout(() => { this.getDataFromServer(); }, 200);
    // }

    async addRec() {
        this.setState({ recs: await (await fetch(`/${this.props.userID}/events`)).json() });


        // this.setState({recs: reco.append()})
    }

    render() {
        return <div>
            <NewRecButton className="btn white-text" getRec = {this.addRec.bind(this)}/>
            <h3>Here are your recommendations:</h3>
            <ul>{this.state.recs.map(rec => <Event key={rec.id} event={rec} userID = {this.props.userID}/>)}</ul>
        </div>;
    }
}