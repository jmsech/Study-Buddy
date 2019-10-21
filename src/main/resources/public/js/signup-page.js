class SignUpPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return(
            <div className="login-body">
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

    // Function to validate an email
    validateEmail(email) {
        const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    // Function to validate password
    validatePassword(password) {
        let isValid = true;
        let msg = "Invalid password. Password must follow the following rules:";
        if (password.length < 8 || password.length > 100) {
            msg = msg.concat("\n- Must be at least 8 characters long (and no more than 100 characters long)");
            isValid = false;
        }
        if (!password.match(/[0-9]/g)) {
            msg = msg.concat("\n- Must contain at least one number");
            isValid = false;
        }
        if (!password.match(/[a-z]/g)) {
            msg = msg.concat("\n- Must contain at least one lower case letter");
            isValid = false;
        }
        if (!password.match(/[A-Z]/g)) {
            msg = msg.concat("\n- Must contain at least one upper case letter");
            isValid = false;
        }
        if (!password.match(/[\!\@\#\$\%\^\&\*]/g)) {
            msg = msg.concat("\n- Must contain at least one special character");
            isValid = false;
        }
        if (isValid) {
            return {isValid: true, msg: ""};
        } else {
            return {isValid: false, msg: msg};
        }
    }

    async handleSubmit(event) {
        const emailValidation = this.validateEmail(event.target.email.value);
        const passwordValidation = this.validatePassword(event.target.password.value);
        if (!emailValidation) {
            alert("Invalid email");
            event.preventDefault();
            return;
        } else if (!passwordValidation.isValid) {
            alert(passwordValidation.msg);
            event.preventDefault();
            return;
        } else if (event.target.password.value !== event.target.confirmPassword.value) {
            alert("The passwords must match");
            event.preventDefault();
            return;
        }
        const formData = new FormData();
        formData.append("firstName", event.target.firstName.value);
        formData.append("lastName", event.target.lastName.value);
        formData.append("email", String(event.target.email.value).toLowerCase());
        formData.append("password", event.target.password.value);
        event.target.reset();
        event.preventDefault();
        const response = await (await fetch("/users", {method: "POST", body: formData})).json();
        if (response) { // If response is true, user was created
            alert("User successfully created!");
        } else {
            alert("This email is already associated with a user");
        }
        open("/../index.html", "_self");
    }



    render() {
        return (
            <form id="signUpForm" onSubmit={this.handleSubmit}>
                <div className="input-field">
                    <label htmlFor="firstName">First Name</label>
                    <input id="firstName" name="firstName" type="text" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="lastName">Last Name</label>
                    <input id="lastName" name="lastName" type="text" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="email">Email</label>
                    <input id="email" name="email" type="text" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="password">Password</label>
                    <input id="password" name="password" type="password" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="confirmPassword">Confirm password</label>
                    <input id="confirmPassword" name="confirmPassword" type="password" required/>
                </div>
                <div className="center-align"><button className="btn">Sign Up</button></div>
            </form>
        )
    }
}

ReactDOM.render(<SignUpPage/>, document.querySelector("#signup-page"));