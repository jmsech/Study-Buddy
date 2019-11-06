class User extends React.Component {
    constructor(props) {
        super(props);
        const parameters = location.search.substring(1).split("&");
        const temp = parameters[0].split("=");
        const id = unescape(temp[1]);
        this.state = {userID: id};
    }
    render () {
       return (
           <div className="content-row">
               <div className="column">
                   <h3>Events <GetGoogleEvents userID={this.state.userID}/> </h3>
                   <NewEventButton className="new-event-button btn white-text" flip={this.props.flipEvent} showEventForm={this.props.showEventForm}/>
                   <NewEventForm userID={this.state.userID} showEventForm={this.props.showEventForm} flip={this.props.flipEvent}/>
                   <EventList userID={this.state.userID}/>
               </div>
               <div className="column">
                   <h3>Recommendations</h3>
                   <Recommendations flipRec={this.props.flipRec} showRecForm={this.props.showRecForm} userID={this.state.userID}/>
               </div>
            </div>
       );
    }
}

class GetGoogleEvents extends React.Component {
    async collectEvents() {
        let daysToCollect = 0
        while (daysToCollect < 1) {
            daysToCollect = parseInt(
                prompt("Enter the number of days you would like to sync", "7")
            );
        }
        if (daysToCollect) {
            const formData = new FormData();
            formData.append("userID", this.props.userID);
            formData.append("daysToCollect", daysToCollect)
            await fetch(`../${this.props.userID}`, {method: "POST", body: formData})
                .then(this.handleResponse);
        }
    }

    async handleResponse(response) {
        const msg = await response.json();
        // console.log(msg);
        if (msg === "Access Denied") {
            alert("Google Calender Access Denied");
        } else if (msg === "No Upcoming Events") {
            alert("Your Google Calender has no upcoming events");
        }
        return response;
    }

    render() {
        return(
            <button className="google-calendar-button btn white-text" onClick = {() => this.collectEvents()}/>
            )
    }
}