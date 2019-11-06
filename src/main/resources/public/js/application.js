class Application extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showEventForm: false, showRecForm: false};
    }

    flipEventFormState() {
        this.setState({showEventForm: !this.state.showEventForm});
    }

    flipRecFormState() {
        this.setState({showRecForm: !this.state.showRecForm});
    }

    render() {
        return (
            <div>
                <div>
                    <Header/>
                    <div className="centralized-body">
                        <User showEventForm = {this.state.showEventForm} flipEvent={this.flipEventFormState.bind(this)} showRecForm={this.state.showRecForm} flipRec={this.flipRecFormState.bind(this)}/>
                    </div>
                </div>
            </div>
        );
    }
}

const LogoutButton = () => (
    // TODO make this actually log out of a user
    <a className="btn" href="/../index.html">Log&nbsp;Out</a>
)

const Header = () => (
    <header>
        <img className="logo" src="../images/logo-full.png"/>
        <div className="logout-button"><LogoutButton/></div>
    </header>
);

ReactDOM.render(<Application/>, document.querySelector("#application"));


