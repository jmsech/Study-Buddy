class ProfileInfo extends React.Component {
    constructor(props) {
        super(props);
        this.state = {user: null};
    }

    async getUserData() {
        this.setState({ user: await (await fetch(`/users/${this.props.userId}`)).json() });
    }

    componentDidMount() {
        this.getUserData();
    }

    render() {
        let userName = "";
        let userEmail = "";
        if (this.state.user !== null) {
            userName = this.state.user.name;
            userEmail = this.state.user.email;
        }
        return (
            <div className="center ">
                <h3>Profile</h3><br/>
                <img src="../../images/profile-placeholder.png" className="profile-picture" alt="Missing profile picture"/>
                <h5>{userName}</h5>
                <h5>{userEmail}</h5>
                <hr/>
            </div>
        );
    }
}

class CurrentCourses extends React.Component {
    render() {
        return (
            <div className="center">
                <h4>Your classes</h4>
            </div>
        );
    }
}