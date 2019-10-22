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
                   <h3>Events</h3>
                   <NewEventButton className="new-event-button btn white-text" flip={this.props.flip} showForm={this.props.showForm}/>
                   <NewEventForm userID={this.state.userID} showForm={this.props.showForm} flip={this.props.flip}/>
                   <EventList userID={this.state.userID}/>
               </div>
               <div className="column">
                    <Recommendation userID={this.state.userID}/>
               </div>
            </div>
       );
    }
}