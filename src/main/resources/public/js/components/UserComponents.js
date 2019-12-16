class User extends React.Component {
    render () {
        let firstName = "";
        if (this.props.user !== null) {
            let fullName = this.props.user.name;
            var n = fullName.search(" ");
            firstName = fullName.substr(0, n);
        }
        return (
           <div>
               <h5>Welcome, {firstName}!</h5>
               <SeeCourses userID={this.props.user.id}/>
               {/* Below is the original display on the webpage */}
               <div className="content-row">
                   {/* "Column" splits the page up into as many columns as necessary (in this case 2) */}
                   <div className="eventsColumn">
                       {/* Header which says Events and Google Calendar button */}
                       <h3>Events <GetGoogleEvents userID={this.props.user.id}/> </h3>
                       {/* "New Event" Button */}
                       <NewEventButton className="new-event-button btn white-text" flip={this.props.flipEventFormState} showEventForm={this.props.showEventForm}/>
                       {/* New event form (which is hidden when "New Event" is unclicked) */}
                       <NewEventForm userID={this.props.user.id} showEventForm={this.props.showEventForm} flip={this.props.flipEventFormState}/>
                       {/* List of events which user is attending */}
                       <EventList userID={this.props.user.id}/>
                   </div>
                   <div className="recColumn">
                       {/* Recommendations Header */}
                       <h3 className="center">Recommendations</h3>
                       <div className="content-row">
                       <div className="column">
                            <h5 className="center"> Find a time to study with a group of your buddies </h5>
                            {/* Display list of recommendations */}
                            <Recommendations flipRec={this.props.flipRecFormState} showRecForm={this.props.showRecForm} userID={this.props.user.id}/>
                       </div>
                       <div className="column">
                            <h5 className="center"> Find new buddies to study with in one of your classes </h5>
                            <BuddyRecommendations flipRec={this.props.flipBuddyRecFormState} showBuddyRecForm={this.props.showBuddyRecForm} userID={this.props.user.id}/>
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
        const userId = this.props.userID;
        this.setState({courses: await (await fetch(`/${userId}/courses`)).json()});
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
                <div id="slide-out" className="sidenav teal lighten-1" style={{width: "30%"}}>
                    <span className="card-title">
                        <h4 className="center">Your Courses</h4>
                    </span>
                    <ul>{this.state.courses.map(course => <Course key={course.courseId} course={course} userID={this.props.userID}/>)}</ul>
                </div>
            </div>
        );
    }
}
class Course extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showDeadlineForm: false}
    }

    flipDeadlineFormState() {
        this.setState({showDeadlineForm: !this.state.showDeadlineForm} );
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
                    <DeadlineButton userID={this.props.userID} active={this.state.showDeadlineForm} flip = {this.flipDeadlineFormState.bind(this)}/>
                    <NewDeadlineForm active={this.state.showDeadlineForm} flip = {this.flipDeadlineFormState.bind(this)} courseID={this.props.course.courseId} userID={this.props.userID}/>
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

class DeadlineButton extends React.Component {
    render() {
        let title = "Add Deadline";
        if (this.props.active) {
            title = "Cancel";
        }
        return <button className="btn centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class NewDeadlineForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    async handleResponse(response) {
        return response;
    }

    handleSubmit(deadline) {
        deadline.preventDefault();
        this.props.flip();
        let dueTime = "11:59 PM";
        if (deadline.target.dueTime.value !== "") {
            dueTime = deadline.target.dueTime.value
        }
        let description = " "
        const formData = new FormData();
        // formData.append("userID", this.props.userID);
        formData.append("title", deadline.target.title.value);
        formData.append("courseID", this.props.courseID);
        // combine tim/date into the format yyyy-mm-dd 00:00
        formData.append("dueDate", deadline.target.dueDate.value + " " + dueTime);
        formData.append("description", description);
        fetch(`../${this.props.userID}/deadline`, {method: "POST", body: formData})
            .then(this.handleResponse);
        deadline.target.reset(); // clear the form entries
    }

    componentDidMount() {
        M.Datepicker.init(document.querySelectorAll('.datepicker'), {
            autoClose: true,
            showClearBtn: true,
            format: "yyyy-mm-dd",
            minDate: (new Date())
        });
        M.Timepicker.init(document.querySelectorAll('.timepicker'), {
            showClearBtn: true
        });
    }

    render() {
        let style = {display: "none"};
        if (this.props.active) { style = {display: "block"}};
        let date = new Date();
        // Define default start date
        let defaultStartDate = date.getFullYear()+'-'+(date.getMonth()+1)+'-';
        if (date.getDate() < 10) {
            defaultStartDate = defaultStartDate + "0" + date.getDate();
        } else {
            defaultStartDate = defaultStartDate + date.getDate();
        }
        return (
            <form id="deadline-form" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="title"  className="blue-grey-text text-darken-2">Title</label>
                    <input id="title" name="title" type="text" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="dueDate" className="active blue-grey-text text-darken-2">Due Date</label>
                    <input id="dueDate" type="text" className="datepicker" defaultValue={defaultStartDate} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="dueTime" className="active blue-grey-text text-darken-2">Due Time</label>
                    <input id="dueTime" name="dueTime" type="text" className="timepicker"/>
                </div>
                <button className="btn white-text">Save Deadline</button>
            </form>
        );
    }
}




