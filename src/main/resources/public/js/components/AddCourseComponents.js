class AddCourse extends React.Component {
    constructor(props) {
        super(props);
        //this.state = {};
    }

    render() {
        return (
            <div>
                <AddCourseButton className="new-event-button btn white-text" flip={this.props.flipAddCourse} showAddCourseForm={this.props.showAddCourseForm} />
                <AddCourseForm userID={this.props.userID} showAddCourseForm={this.props.showAddCourseForm} />
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
       // this.state = {value: ''};

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    async handleResponse(response) {
        return response;
    }

    async handleSubmit(addCourse) {
        this.props.flip();
        const formData = new FormData();
        formData.append("userId", this.props.userID);

        //get course Num
        let courseID = addCourse.target.courseNumber.value;
        courseID = courseID.replaceAll(" ", "");

        let date = new Date();
        let year = date.getFullYear();
        let month = date.getMonth();
        let monthStr = (month < 6) ? "Sp" : "Fa";

        courseID = courseID + "(01)" + monthStr + year;
        formData.append("courseId", courseID);

        console.log(courseID);
        addCourse.preventDefault();
        fetch(`../${this.props.userID}/courses2/`, {method: "POST", body: formData})
            .then(this.handleResponse);
        addCourse.target.reset(); // clear the form entries
    }


    render() {
        let style = {display: "none"};
        if (this.props.showAddCourseForm) { style = {display: "block"} }

        return (
            <form id="addCourseForm" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="courseNumber">Course Number (ex EN.601.226)</label>
                    <input id="courseNumber" name="courseNumber" type="text" required/>
                </div>
                <button className="btn white-text">Add Course!</button>
            </form>
        );
    }
}