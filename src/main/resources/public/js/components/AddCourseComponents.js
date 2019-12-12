class AddCourse extends React.Component {
    constructor(props) {
        super(props);
        //this.state = {};
    }

    render() {
        return (
            <div>
                <AddCourseButton className="new-event-button btn white-text" flip={this.props.flipAddCourse} showAddCourseForm={this.props.showAddCourseForm} />
                <AddCourseForm userID={this.props.userID} showAddCourseForm={this.props.showAddCourseForm} flip={this.props.flipAddCourse}/>
            </div>
        )
    }
}

class AddCourseButton extends React.Component {
    render() {
        let title = "Add Course";
        if (this.props.showAddCourseForm) {
            title = "Cancel";
        }
        return <button className="btn cyan darken-3 centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class AddCourseForm extends React.Component {
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
        this.setState({ courses: await (await fetch("/courses")).json() });
    }

    async handleResponse(response) {
        return response;
    }

    async handleSubmit(addCourse) {
        addCourse.preventDefault();
        this.props.flip();
        const formData = new FormData();
        formData.append("userId", this.props.userId);

        //get course Num
        let courseId = addCourse.target.courseNumber.value;
        courseId = courseId.substr(0, courseId.indexOf(")") + 1);
        courseId = courseId.replace(/ /g, "");

        let date = new Date();
        let year = date.getFullYear();
        let month = date.getMonth();
        let monthStr = (month < 6) ? "Sp" : "Fa";

        courseId = courseId.concat(monthStr, year)
        formData.append("courseId", courseId);

        console.log(courseId);
        fetch(`../${this.props.userId}/courses/`, {method: "POST", body: formData})
            .then(this.handleResponse);
        addCourse.target.reset(); // clear the form entries
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
        M.Autocomplete.init(document.querySelectorAll('.autocomplete'), options);

        let style = {display: "none"};
        if (this.props.showAddCourseForm) { style = {display: "block"} }

        return (
            <form id="addCourseForm" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="courseNumber">Course Name or Number (ex EN.601.226(01))</label>
                    <input id="courseNumber" name="courseNumber" type="text" className="autocomplete" required/>
                </div>
                <button className="btn white-text">Add Course!</button>
            </form>
        );
    }
}