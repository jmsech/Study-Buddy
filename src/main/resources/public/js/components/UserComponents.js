class User extends React.Component {
    constructor(props) {
        super(props);
        this.state = {userID: 1};
    }
    render () {
       return <div>
           <NewEventForm userID = {this.state.userID} showForm = {this.props.showForm} flip={this.props.flip}/>
           <EventList userID = {this.state.userID}/>
           <RecList userID={this.state.userID}/>
       </div>
    }
}