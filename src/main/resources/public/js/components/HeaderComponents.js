class LogoutButton extends React.Component {
    render() {
        return (
            <button className="btn" onClick = {() => this.logOut()}>Log&nbsp;Out</button>
        );
    }

    async logOut() {
        await fetch("/users/authenticate", {method: "DELETE"});
        open("/", "_self");
    }
}

class ProfileButton extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <button className="btn" onClick={() => open(this.props.url, "_self")}>Your&nbsp;Profile</button>
        );
    }
}

class Header extends React.Component {
    render() {
        const homeUrl = "/";

        const profileUrl = "/profile/profile.html";
        return (
            <header>
                <a href={homeUrl}>
                    <img className="logo" src="../../images/logo-full.png" alt="Home page"/>
                </a>
                <div className="header-buttons">
                    <ProfileButton url={profileUrl}/>&nbsp;&nbsp;&nbsp;&nbsp;<LogoutButton/>
                </div>
            </header>
        );
    }
}
