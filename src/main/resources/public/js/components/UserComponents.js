class User extends React.Component {
    render () {
       return (
           <div>
               <AddCourses active={this.props.showCourseDisplay} flip={this.props.flipCourseDisplay} userID={this.props.userId}/>
               {/* Below is the original display on the webpage */}
               <div className="content-row">
                   {/* "Column" splits the page up into as many columns as necessary (in this case 2) */}
                   <div className="column">
                       {/* Header which says Events and Google Calendar button */}
                       <h3>Events <GetGoogleEvents userID={this.props.userId}/> </h3>
                       {/* "New Event" Button */}
                       <NewEventButton className="new-event-button btn white-text" flip={this.props.flipEventFormState} showEventForm={this.props.showEventForm}/>
                       {/* New event form (which is hidden when "New Event" is unclicked) */}
                       <NewEventForm userID={this.props.userId} showEventForm={this.props.showEventForm} flip={this.props.flipEventFormState}/>
                       {/* List of events which user is attending */}
                       <EventList userID={this.props.userId}/>
                   </div>
                   <div className="column">
                       {/* Recommendations Header */}
                       <h3>Recommendations</h3>
                       {/* Display list of recommendations */}
                       <Recommendations flipRec={this.props.flipRecFormState} showRecForm={this.props.showRecForm} userID={this.props.userId}/>
                   </div>
               </div>
           </div>
       );
    }
}

class GetGoogleEvents extends React.Component {
    async collectEvents() {
        let daysToCollect = 0;
        while (daysToCollect < 1) {
            daysToCollect = parseInt(
                prompt("Enter the number of days you would like to sync", "7")
            );
        }
        if (daysToCollect) {
            const formData = new FormData();
            formData.append("userID", this.props.userID);
            formData.append("daysToCollect", daysToCollect);
            await fetch(`../${this.props.userID}`, {method: "POST", body: formData})
                .then(this.handleResponse);
        }
    }

    async handleResponse(response) {
        const msg = await response.json();
        // console.log(msg);
        if (msg === "Access Denied") {
            alert("Google Calender Access Denied");
        } else if (msg === "No Upcoming Events") {
            alert("Your Google Calender has no upcoming events");
        }
        return response;
    }

    render() {
        return(
            <button className="google-calendar-button btn white-text" onClick = {() => this.collectEvents()}/>
            );
    }
}

class AddCourses extends React.Component {
    render() {
        let text = "See Courses";
        if (this.props.active) {text = "Hide Courses";}
        let disp = {display: "none"};
        if (this.props.active) { disp = {display: "block"}; }
        return (
            <div>
                <button className="btn" onClick = {() => this.props.flip()}> {text} </button>
                <CourseList disp={disp} userID = {this.props.userID}/>
            </div>
        )
    }
}

class CourseList extends React.Component {
    constructor(props) {
        super(props);
        this.state = { courses: [{id: 1, name: "OOSE", location: "merg", courseNumber: "601.400", professor: "Fettuccine"}, {id:2, name:"algos", location:"shaffer", courseNumber: "601.433", professor: "Dinitz"}, {id: 1, name: "OOSE", location: "merg", courseNumber: "601.400", professor: "Fettuccine"}, {id:2, name:"algos", location:"shaffer", courseNumber: "601.433", professor: "Dinitz"}, {id: 1, name: "OOSE", location: "merg", courseNumber: "601.400", professor: "Fettuccine"}, {id:2, name:"algos", location:"shaffer", courseNumber: "601.433", professor: "Dinitz"}, {id: 1, name: "OOSE", location: "merg", courseNumber: "601.400", professor: "Fettuccine"}, {id:2, name:"algos", location:"shaffer", courseNumber: "601.433", professor: "Dinitz"}] };
    }

    async getDataFromServer() {
        await fetch(`/${this.props.userID}/courses`, {method: "PUT"}); {/* FIXME does this line do anthing?*/}
        this.setState({ courses: await (await fetch(`/${this.props.userID}/courses`)).json() });
        window.setTimeout(() => { this.getDataFromServer(); }, 200);
    }

    async componentDidMount() {
        await this.getDataFromServer();
    }

    render() {
        return (
            <ul className="list-inline">{this.state.courses.map(course =>
                <Course disp = {this.props.disp}
                        key={course.id}
                        course={course}
                        userID={this.props.userID}
                        disp={this.props.disp}
                />)}
            </ul>
        );
    }
}

class Course extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <li className="card hoverable teal lighten-2">
                <div className="card-content black-text" style={this.props.disp}>
                    <span className="card-title">
                        {/*<CourseHeader course={this.props.course}/>*/}
                        <h6>{this.props.course.courseName}</h6>
                    </span>
                    <p>{this.props.course.courseNumber + " (" + this.props.course.section + ") "}</p>
                    {/*<p>{this.props.course.courseDescription}</p> //FIXME: This is very long*/}
                    <p>{this.props.course.instructor}</p>
                    <p>{this.props.course.timeString}</p>
                    <CourseLocation course={this.props.course}/>
                </div>
            </li>
        );
    }
}

class CourseLocation extends React.Component {
    constructor(props) {
        super(props);
        this.state = null;
    }

    render() {
        if (this.props.course.location !== "" && this.props.course.description !== null){
            return (
                <div>
                    <p><i className="tiny material-icons">location_on</i>{this.props.course.location}</p>
                </div>
            );
        } else return null;
    }
}






