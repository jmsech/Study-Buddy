class LoginPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return(
            <div className="login-body">
                <h1>Welcome to StudyBuddy!</h1>
                <LoginForm/>
                <div className="right-align"><a href="/signup/signup.html">Sign up</a></div>
            </div>
        )
    }
}

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = { id: -1 };

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async handleSubmit(event) {
        const formData = new FormData();
        formData.append("email", String(event.target.email.value).toLowerCase());
        formData.append("password", event.target.password.value);
        event.target.reset();
        event.preventDefault();
        // Validate username and password
        const response = await (await fetch("/users/authenticate/", {method: "POST", body: formData})).json();
        if (response !== 0 && response !== -1) {
            location.reload();
        } else if (response === 0) {
            alert("Invalid email/password combination");
        } else {
            alert("Congrats, you found the easter egg!");
        }
    }

    render() {
        return (
            <form id="loginForm" onSubmit={this.handleSubmit}>
                <div className="input-field">
                    <label htmlFor="email">Email</label>
                    <input id="email" name="email" type="email" className="validate" required/>
                    <span className="helper-text" data-error="Invalid email"/>
                </div>
                <div className="input-field">
                    <label htmlFor="password">Password</label>
                    <input id="password" name="password" type="password" required/>
                </div>
                <div className="center-align"><button className="btn">Log In</button></div>
            </form>
        )
    }
}