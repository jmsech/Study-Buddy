class LogoutButton extends React.Component {
    render() {
        return (
            // TODO make this actually log out of a user
            <a className="btn" href="../../index.html" onClick = {() => this.logOut()}>Log&nbsp;Out</a>
        );
    }

    async logOut() {
        await fetch(`../${this.props.userID}`, {method: "DELETE"})
    }
}

class ProfileButton extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <a className="btn" href={this.props.url}>Your&nbsp;Profile</a>
        );
    }
}

class Header extends React.Component {
    render() {
        const baseHomeUrl = "/application/application.html?id=";
        const homeUrl = baseHomeUrl.concat(this.props.userId);

        const baseProfileUrl = "/profile/profile.html?id=";
        const profileUrl = baseProfileUrl.concat(this.props.userId);
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
