class Application extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showForm: false};
    }

    flipFormState() {
        this.setState({showForm: !this.state.showForm});
    }

    render() {
        return (
            <div>
                <div className="right-align logout-button"><LogoutButton/></div>
                <div className="centralized-body">
                    <Header/>
                    <NewEventButton className="new-event-button btn white-text" flip={this.flipFormState.bind(this)} showForm={this.state.showForm}/>
                    <User showForm = {this.state.showForm} flip={this.flipFormState.bind(this)}/>
                </div>
            </div>
        );
    }
}

const LogoutButton = () => (
    // TODO make this actually log out of a user
    <a className="btn" href="/../index.html">Log Out</a>
)

const Header = () => (
    <header>
    <h1>StudyBuddy</h1>
</header>
);

ReactDOM.render(<Application/>, document.querySelector("#application"));


