class Recommendation extends React.Component {
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
                <NewRecButton getRec = {this.getRec.bind(this)}/>
                <RecList userID={this.props.userID} recs={this.state.recs} clearRecs={this.clearRecs.bind(this)}/>
            </div>
        )
    }
}

class NewRecButton extends React.Component {
    render() {
        let title = "Generate a Recommendation";
        return <button className="btn" onClick={() => { this.props.getRec() }}>{title}</button>;
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
                <div className="card-action right-align">
                    <RecDeleteButton event={this.props.event} userID = {this.props.userID} clearRecs={this.props.clearRecs}/>
                </div>
            </li>
        );
    }
}

class RecList extends React.Component {
    render() {
        return <div>
            <h3>Here are your recommendations:</h3>
            <ul>{this.props.recs.map(rec => <Rec key={rec.id} event={rec} userID={this.props.userID} clearRecs={this.props.clearRecs}/>)}</ul>
        </div>;
    }
}

class RecDeleteButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <button className="btn cyan darken-3" onClick={() => {this.props.clearRecs()}}><i className="material-icons">delete</i></button>
        )
    }
}