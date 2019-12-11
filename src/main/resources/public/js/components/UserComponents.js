class User extends React.Component {
    render () {
       return (
           <div>
               <SeeCourses active={this.props.showCourseDisplay} flip={this.props.flipCourseDisplay} userID={this.props.userId}/>
               <AddCourse flipAddCourse={this.props.flipAddCourseFormState} showAddCourseForm={this.props.showAddCourseForm} userID={this.props.userID}/>
               {/* Below is the original display on the webpage */}
               <div className="content-row">
                   {/* "Column" splits the page up into as many columns as necessary (in this case 2) */}
                   <div className="eventsColumn">
                       {/* Header which says Events and Google Calendar button */}
                       <h3>Events <GetGoogleEvents userID={this.props.userId}/> </h3>
                       {/* "New Event" Button */}
                       <NewEventButton className="new-event-button btn white-text" flip={this.props.flipEventFormState} showEventForm={this.props.showEventForm}/>
                       {/* New event form (which is hidden when "New Event" is unclicked) */}
                       <NewEventForm userID={this.props.userId} showEventForm={this.props.showEventForm} flip={this.props.flipEventFormState}/>
                       {/* List of events which user is attending */}
                       <EventList userID={this.props.userId}/>
                   </div>
                   <div className="recColumn">
                       {/* Recommendations Header */}
                       <h3 className="center">Recommendations</h3>
                       <div className="content-row">
                       <div className="column">
                            <h5 className="center"> Find a time to study with a group of your buddies </h5>
                            {/* Display list of recommendations */}
                            <Recommendations flipRec={this.props.flipRecFormState} showRecForm={this.props.showRecForm} userID={this.props.userId}/>
                       </div>
                       <div className="column">
                            <h5 className="center"> Find new buddies to study with in one of your classes </h5>
                            <BuddyRecommendations flipRec={this.props.flipBuddyRecFormState} showBuddyRecForm={this.props.showBuddyRecForm} userID={this.props.userId}/>
                       </div>
                       </div>
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

class SeeCourses extends React.Component {
    render() {
        let text = "See Courses";
        return (
            <div>
                <button data-target="slide-out" className="btn sidenav-trigger"> {text} </button>
                <CourseList userID = {this.props.userID}/>
            </div>
        )
    }
}

class CourseList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            courses: []
        };
    }

    async getDataFromServer() {
        this.setState({courses: await (await fetch(`/${this.props.userID}/courses`)).json()});
        window.setTimeout(() => {
            this.getDataFromServer();
        }, 200);
    }

    async componentDidMount() {
        await this.getDataFromServer();
        M.Sidenav.init(document.querySelectorAll('.sidenav'), {
        });
    }

    render() {
        return (
            <div>
                <ul id="slide-out" className="sidenav">
                    <span className="card-title">
                        <h4>Your Courses</h4>
                    </span>
                    {this.state.courses.map(course => <Course key={course.id} course={course}/>)}
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
            <div className="card hoverable teal lighten-2">
                <div className="card-content black-text">
                    <span className="card-title">
                        {/*<CourseHeader course={this.props.course}/>*/}
                        <h6>{this.props.course.courseName}</h6>
                    </span>
                    <p>{this.props.course.courseNumber + " (" + this.props.course.section + ") "}</p>
                    {/*<p>{this.props.course.courseDescription}</p> //FIXME: This is very long*/}
                    <p>{this.props.course.instructor}</p>
                    <p>{this.props.course.timeString}</p>
                    <CourseLocation course={this.props.course}/>
                    <button className="btn">Add Deadline</button>
                </div>
            </div>
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






