class LoginPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return(
            <div className="centralized-body">
                <h1>Welcome to StudyBuddy!</h1>
                <LoginForm/>
            </div>
        )
    }
}

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    openApplication() {
        location.href="./application.html";
    }

    handleSubmit(event) {
        const formData = new FormData();
        formData.append("username", event.target.username.value);
        formData.append("password", event.target.username.password);
        event.target.reset();
        // TODO Validate username and password
        if (true) {
            this.openApplication();
        }
    }

    render() {
        return (
            <form id="loginForm">
                <div className="input-field">
                    <label htmlFor="username">Username</label>
                    <input id="username" name="username" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="password">Password</label>
                    <input id="password" name="password" type="password"/>
                </div>
                <div className="center-align"><a className="btn" href="application.html">Login</a></div>
            </form>
        )
    }
}

ReactDOM.render(<LoginPage/>, document.querySelector("#login-page"));