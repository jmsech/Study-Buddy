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
                <div>
                    <Header/>
                    <div className="centralized-body">
                        <User showForm = {this.state.showForm} flip={this.flipFormState.bind(this)} showForm={this.state.showForm}/>
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


