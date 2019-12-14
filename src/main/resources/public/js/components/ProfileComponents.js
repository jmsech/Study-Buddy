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
                <CollapsibleCourseList userId={this.props.userId}/>
                <div className="content-row">
                <div className="column">
                <AddCourse flipAddCourse={this.props.flipAddCourseFormState} showAddCourseForm={this.props.showAddCourseForm} userId={this.props.userId}/>
                </div>
                <div className="column">
                <RemoveCourse flipRemoveCourse={this.props.flipRemoveCourseFormState} showRemoveCourseForm={this.props.showRemoveCourseForm} userId={this.props.userId}/>
                </div>
                </div>
            </div>
        );
    }
}

class CollapsibleCourseList extends React.Component {
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
        M.Collapsible.init(document.querySelectorAll('.collapsible'), {});
    }

    render() {
        return (
            <div>
                <ul className="collapsible popout">
                    {this.state.courses.map(course => <CollapsibleCourse key={course.courseId} course={course}/>)}
                </ul>
            </div>
        );
    }
}

class CollapsibleCourse extends React.Component {
    render() {
        return (
            <li>
                <div className="collapsible-header teal lighten-2">{this.props.course.courseName}</div>
                <div className="collapsible-body teal lighten-2">
                    <p className="collapsible-course-body">
                        {this.props.course.courseNumber + " (" + this.props.course.section + ") "}<br/>
                        {this.props.course.instructor}<br/>
                        {this.props.course.timeString}<br/>
                        <i className="tiny material-icons">location_on</i>{this.props.course.location}
                    </p>
                </div>
            </li>
        );
    }
}


class RemoveCourse extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <RemoveCourseButton className="btn cyan darken-3 centralized-button" userId={this.props.userId} flip={this.props.flipRemoveCourse} showRemoveCourseForm={this.props.showRemoveCourseForm} />
                <RemoveCourseForm userId={this.props.userId} showRemoveCourseForm={this.props.showRemoveCourseForm} flip={this.props.flipRemoveCourse}/>
            </div>
        )
    }
}

class RemoveCourseButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = {courses: []};
    }

    render() {
        let title = "Remove Course";
        if (this.props.showRemoveCourseForm) {
            title = "Cancel";
        }
        return <button className="btn cyan darken-3 centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class RemoveCourseForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {courses: []};

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    componentDidMount() {
        // Get course information for the autocomplete
        this.getDataFromServer();
    }

    async getDataFromServer() {
        this.setState({ courses: await (await fetch(`/${this.props.userId}/courses`)).json() });
    }

    async handleResponse(response) {
        return response;
    }

    async handleSubmit(RemoveCourse) {
        RemoveCourse.preventDefault();

        this.props.flip();
        const formData = new FormData();
        formData.append("userId", this.props.userId);

        //get course Num
        let courseId = RemoveCourse.target.courseNumber.value;
        courseId = courseId.substr(0, courseId.indexOf(")") + 1);
        courseId = courseId.replace(/ /g, "");

        let date = new Date();
        let year = date.getFullYear();
        let month = date.getMonth();
        let monthStr = (month < 6) ? "Sp" : "Fa";

        courseId = courseId.concat(monthStr, year)
        formData.append("courseId", courseId);

        const basePath = `../${this.props.userId}/courses`;
        fetch(basePath, {method: "DELETE", body: formData})
        RemoveCourse.target.reset(); // clear the form entries
    }


    render() {
        let courseData = {};
        for (let i = 0; i < this.state.courses.length; i++) {
            const course = this.state.courses[i];
            const courseNumber = course.courseNumber;
            const courseSection = course.section;
            const courseName = course.courseName;
            const fullCourseName = courseNumber.concat("(", courseSection, ") - ", courseName);
            Object.assign(courseData, {[fullCourseName]: null});
        }
        const options = {data: courseData, limit: 20};

        // Initialize materialize autocomplete
        M.Autocomplete.init(document.querySelectorAll('.autocompleteRemoveCourse'), options);

        let style = {display: "none"};
        if (this.props.showRemoveCourseForm) { style = {display: "block"} }

        return (
            <form id="RemoveCourseForm" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="courseNumber">Course Name or Number </label>
                    <input id="courseNumber" name="courseNumber" type="text" className="autocompleteRemoveCourse" required/>
                </div>
                <button className="btn white-text">Remove Course!</button>
            </form>
        );
    }
}

