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
                <div className="right-align"><a href="./signup.html">Sign up</a></div>
            </div>
        )
    }
}

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    handleSubmit(event) {
        const formData = new FormData();
        formData.append("email", event.target.email.value);
        formData.append("password", event.target.password.value);
        event.target.reset();
        // Validate username and password
        const response = fetch("/users/authenticate", {body: formData}).json();
        if (true) {
            open("/application.html", "_self");
        }
    }

    render() {
        return (
            <form id="loginForm" onSubmit={this.handleSubmit}>
                <div className="input-field">
                    <label htmlFor="email">Email</label>
                    <input id="email" name="email" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="password">Password</label>
                    <input id="password" name="password" type="password"/>
                </div>
                <div className="center-align"><button className="btn">Log In</button></div>
            </form>
        )
    }
}

ReactDOM.render(<LoginPage/>, document.querySelector("#login-page"));