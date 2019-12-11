class ProfileInfo extends React.Component {
    constructor(props) {
        super(props);
        this.state = {user: null};
    }

    async getUserData() {
        this.setState({ user: await (await fetch(`/users/${this.props.userId}`)).json() });
    }

    componentDidMount() {
        this.getUserData();
    }

    render() {
        let userName = "";
        let userEmail = "";
        if (this.state.user !== null) {
            userName = this.state.user.name;
            userEmail = this.state.user.email;
        }
        return (
            <div className="center ">
                <h3>Profile</h3><br/>
                <img src="../../images/profile-placeholder.png" className="profile-picture" alt="Missing profile picture"/>
                <h5>{userName}</h5>
                <h5>{userEmail}</h5>
                <hr/>
            </div>
        );
    }
}

class CurrentCourses extends React.Component {
    render() {
        return (
            <div className="center">
                <h4>Your classes</h4>
                <CollapsibleCoursesList userId={this.props.userId}/>
            </div>
        );
    }
}

class CollapsibleCoursesList extends React.Component {
    constructor(props) {
        super(props);
        this.state = { courses: [] };
    }

    async getDataFromServer() {
        this.setState({ courses: await (await fetch(`/${this.props.userId}/courses`)).json() });
        window.setTimeout(() => {this.getDataFromServer();}, 200);
    }

    componentDidMount() {
        this.getDataFromServer();
        // Initialize materialize collapsible
        document.addEventListener('DOMContentLoaded', function() {
            const elems = document.querySelectorAll('.collapsible');
            M.Collapsible.init(elems, {});
        });
    }

    render() {
        return (
            <div>
                <ul className="collapsible popout">
                    <li>
                        <div className="collapsible-header">Test header</div>
                        <div className="collapsible-body"><span>Test body</span></div>
                    </li>
                    {this.state.courses.map(course => <CollapsibleCourse key={course.courseId} course={course}/>)}</ul>
            </div>
        );
    }
}

class CollapsibleCourse extends React.Component {
    render() {
        return (
            <li>
                <div className="collapsible-header">{this.props.course.courseName}</div>
                <div className="collapsible-body">yeet</div>
            </li>
        );
    }
}