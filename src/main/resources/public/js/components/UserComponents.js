class User extends React.Component {
    constructor(props) {
        super(props);
        const parameters = location.search.substring(1).split("&");
        const temp = parameters[0].split("=");
        const id = unescape(temp[1]);
        this.state = {userID: id};
    }
    render () {
       return <div>
           <NewEventForm userID={this.state.userID} showForm={this.props.showForm} flip={this.props.flip}/>
           <EventList userID={this.state.userID}/>
           <Recommendation userID={this.state.userID}/>
       </div>
    }
}