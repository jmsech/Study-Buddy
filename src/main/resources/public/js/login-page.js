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
        this.state = { id: -1 };

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async handleSubmit(event) {
        const email = event.target.email.value;
        const password = event.target.password.value;
        event.target.reset();
        event.preventDefault();
        // Validate username and password
        // const response = fetch(`/users/authenticate/${email}/${password}`)
        //     .then((response) => (response.json()));
        const response = await (await fetch(`/users/authenticate/${email}/${password}`)).json();
        console.log(response);
        if (response !== 0) {
            open("/application.html", "_self");
        } else {
            alert("Invalid email/password combination");
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