





class AddCourses extends React.Component {
    render() {
        let text = "See Courses";
        if (this.props.active) {text = "Hide Courses";}
        let disp = {display: "none"};
        if (this.props.active) { disp = {display: "block"}; }
        return (
            <div>
                <button onClick = {() => this.props.flip()}> {text} </button>
                <CourseList style={disp} userID = {this.props.userID}/>
            </div>
        )
    }
}

class CourseList extends React.Component {
    constructor(props) {
        super(props);
        this.state = { courses: [{id: 1, name: "OOSE"}] };
    }

    async getDataFromServer() {
        await fetch(`/${this.props.userID}/courses`, {method: "PUT"}); {/* FIXME does this line do anthing?*/}
        this.setState({ courses: await (await fetch(`/${this.props.userID}/courses`)).json() });
        window.setTimeout(() => { this.getDataFromServer(); }, 200);
    }

    async componentDidMount() {
        // await this.getDataFromServer();
    }

    render() {
        return (
            <div style={this.props.disp}>
                <ul>{this.state.courses.map(course =>
                    <Course key={course.id}
                            course={course}
                            userID={this.props.userID}
                            disp={this.props.disp}
                    />)}
                </ul>
            </div>
        );
    }
}

class Course extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="card-content black-text" tyle={this.props.disp}>
                <span className="card-title">
                    {/*<CourseHeader course={this.props.course}/>*/}
                    <h4 style={this.props.disp}>{this.props.course.name}</h4>
                </span>
                {/*<CourseProffessor event={this.props.course}/>*/}
                {/*<CourseDesription event={this.props.course}/>*/}
                {/*<CourseStatus event={this.props.course}/>*/}
            </div>
        );
    }
}