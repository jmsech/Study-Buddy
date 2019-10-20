class SignUpPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return(
            <div className="centralized-body">
                <h1>Welcome to StudyBuddy!</h1>
                <SignUpForm/>
                <div className="right-align"><a href="/../index.html">Sign in</a></div>
            </div>
        )
    }
}

class SignUpForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        const formData = new FormData();
        formData.append("firstName", event.target.firstName.value);
        formData.append("lastName", event.target.lastName.value);
        formData.append("email", event.target.email.value);
        formData.append("password", event.target.password.value);
        event.target.reset();
        fetch("/users", {method: "POST", body: formData});
        // TODO - handle case where user wasn't created (case where it already exists, for example)
        alert("User successfully created!");
        open("/../index.html", "_self");
        event.preventDefault();
    }

    render() {
        // TODO - check if email is valid, check if password has at least 8 characters,
        //  compare password and confirmed password
        return (
            <form id="signUpForm" onSubmit={this.handleSubmit}>
                <div className="input-field">
                    <label htmlFor="firstName">First Name</label>
                    <input id="firstName" name="firstName" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="lastName">Last Name</label>
                    <input id="lastName" name="lastName" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="email">Email</label>
                    <input id="email" name="email" type="text"/>
                </div>
                <div className="input-field">
                    <label htmlFor="password">Password</label>
                    <input id="password" name="password" type="password"/>
                </div>
                <div className="input-field">
                    <label htmlFor="confirmPassword">Confirm password</label>
                    <input id="confirmPassword" name="confirmPassword" type="password"/>
                </div>
                <div className="center-align"><button className="btn">Sign Up</button></div>
            </form>
        )
    }
}

ReactDOM.render(<SignUpPage/>, document.querySelector("#signup-page"));