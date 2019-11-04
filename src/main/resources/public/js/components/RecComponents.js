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
                <h3>Recommendations</h3>
                <NewRecButton getRec = {this.getRec.bind(this)}/>
                <RecList userID={this.props.userID} recs={this.state.recs} clearRecs={this.clearRecs.bind(this)}/>
            </div>
        )
    }
}

class NewRecButton extends React.Component {
    render() {
        let title = "Generate a Recommendation";
        return <button className="btn cyan darken-3 centralized-button" onClick={() => { this.props.getRec() }}>{title}</button>;
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
                <div id="recommendation-actions" className="card-action">
                    <RecAcceptButton event={this.props.event} userID={this.props.userID} clearRecs={this.props.clearRecs}/>
                    <RecDeclineButton clearRecs={this.props.clearRecs}/>
                </div>
            </li>
        );
    }
}

class RecList extends React.Component {
    render() {
        return (
            <div>
                <ul>{this.props.recs.map(rec => <Rec key={rec.id} event={rec} userID={this.props.userID} clearRecs={this.props.clearRecs}/>)}</ul>
            </div>
        );
    }
}

class RecAcceptButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    formatAMPM(hours, minutes) {
        var ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours
        minutes = minutes < 10 ? '0'+ minutes : minutes;
        var strTime = hours + ':' + minutes + ' ' + ampm;
        return strTime;
    }

    handleClick() {
        const event = this.props.event;
        const formData = new FormData();
        formData.append("userID", this.props.userID);
        formData.append("title", event.title);
        // Format startTime
        let startTime = String(event.startTime.year).concat("-").concat(event.startTime.monthValue).concat("-");
        if (event.startTime.dayOfMonth < 10) {
            startTime = startTime.concat("0").concat(event.startTime.dayOfMonth);
        } else {
            startTime = startTime.concat(event.startTime.dayOfMonth);
        }
        startTime = startTime.concat(" ").concat(this.formatAMPM(event.startTime.hour, event.startTime.minute));
        // Format endTime
        let endTime = String(event.startTime.year).concat("-").concat(event.endTime.monthValue).concat("-");
        if (event.endTime.dayOfMonth < 10) {
            endTime = endTime.concat("0").concat(event.endTime.dayOfMonth);
        } else {
            endTime = endTime.concat(event.endTime.dayOfMonth);
        }
        endTime = endTime.concat(" ").concat(this.formatAMPM(event.endTime.hour, event.endTime.minute));
        formData.append("startTime", startTime);
        formData.append("endTime", endTime);
        formData.append("description", event.description);
        this.props.clearRecs();
        fetch(`../${this.props.userID}/events`, {method: "POST", body: formData});
    }

    render() {
        return (
            <button className="btn cyan darken-3" onClick={() => {this.handleClick()}}>Accept</button>
        )
    }
}

class RecDeclineButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        return (
            <button className="btn cyan darken-3" onClick={() => {this.props.clearRecs()}}>Decline</button>
        )
    }
}