class Recommendation extends React.Component {
    constructor(props) {
        super(props);
        this.state = { recs: [] };
    }

    async getRec() {
        this.setState({recs: await (await fetch(`/${this.props.userID}/recs`)).json()});
    }

    render() {
        return (
            <div>
                <NewRecButton getRec = {this.getRec.bind(this)}/>
                <RecList userID={this.props.userID} recs = {this.state.recs}/>
            </div>
        )
    }
}

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
    render() {
        return <div>
            <h3>Here are your recommendations:</h3>
            <ul>{this.props.recs.map(rec => <Event key={rec.id} event={rec} userID = {this.props.userID}/>)}</ul>
        </div>;
    }
}