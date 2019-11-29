class Application extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showEventForm: false, showRecForm: false, showCourseDisplay: false}
    }

    flipEventFormState() {
        this.setState({showEventForm: !this.state.showEventForm});
    }

    flipRecFormState() {
        this.setState({showRecForm: !this.state.showRecForm});
    }

    flipCourseDisplay() {
        this.setState({showCourseDisplay: !this.state.showCourseDisplay});
    }

    render() {
        return (
            <div>
                <div>
                    <Header/>
                    <div className="centralized-body">
                        <User showCourseDisplay = {this.state.showCourseDisplay}
                              flipCourseDisplay = {this.flipCourseDisplay.bind(this)}
                              showEventForm = {this.state.showEventForm}
                              flipEventFormState={this.flipEventFormState.bind(this)}
                              showRecForm={this.state.showRecForm}
                              flipRecFormState={this.flipRecFormState.bind(this)}/>
                    </div>
                </div>
            </div>
        );
    }
}

class LogoutButton extends React.Component {
    render() {
        return (
            // TODO make this actually log out of a user
            <a className="btn" href="/../index.html" onClick = {() => this.logOut()}>Log&nbsp;Out</a>
        );
    }

    async logOut() {
        await fetch(`../${this.props.userID}`, {method: "DELETE"})
    }
}

const Header = () => (
    <header>
        <img className="logo" src="../images/logo-full.png"/>
        <div className="logout-button"><LogoutButton/></div>
    </header>
);

ReactDOM.render(<Application/>, document.querySelector("#application"));


