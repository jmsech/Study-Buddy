class BuddyRecommendations extends React.Component {
    constructor(props) {
        super(props);
        this.state = {recs: []};
    }

    clearRec(id) {
        let recToRemove;
        for (let i = 0; i < this.state.recs.length; i++) {
            if (this.state.recs[i].id === id) {
                recToRemove = this.state.recs[i];
            }
        }
        const filteredArray = this.state.recs.filter(function (e) {
            return e !== recToRemove
        });
        this.setState({recs: filteredArray});
    }

    clearRecs() {
        this.setState({recs: []});
    }

    setRecs(recs) {
        this.setState({recs: recs});
    }

    render() {
        return (
            <div>
                <NewBuddyRecButton className="new-event-button btn white-text" flip={this.props.flipRec}
                                   showBuddyRecForm={this.props.showBuddyRecForm}/>
                <NewBuddyRecForm userID={this.props.userID} showBuddyRecForm={this.props.showBuddyRecForm}
                                 flip={this.props.flipRec} setRecs={this.setRecs.bind(this)}/>
                <RecList userID={this.props.userID} recs={this.state.recs} clearRecs={this.clearRecs.bind(this)}
                         clearRec={this.clearRec.bind(this)}/>
            </div>
        )
    }
}

class NewBuddyRecButton extends React.Component {
    render() {
        let title = "Buddy Recommendation";
        if (this.props.showBuddyRecForm) {
            title = "Cancel";
        }
        return <button className="btn cyan darken-3 centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
    }
}

class NewBuddyRecForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: '', courses: []};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    handleChange(rec) {
        this.setState({value: rec.target.value});
    }

    async handleResponse(response) {
        const data = await response.json();
        if (data === "RecPeriodError") {
            alert("Invalid recommendation period (start has to be before end)");
        } else if (data === "NoRecsToDisplay") {
            alert("There are no times in the specified time period where everyone is available")
        } else {
            this.props.setRecs(data);
        }
        return response;
    }

    async handleSubmit(rec) {
        rec.preventDefault();
        this.props.flip();
        const formData = new FormData();
        //reformat course number
        let fullCourseName = rec.target.courseNumber.value;
        let n = fullCourseName.search(" ");
        let courseNum = fullCourseName.substr(0, n);

        formData.append("userId", this.props.userID);
        formData.append("courseNumber", courseNum);

        formData.append("startTime", rec.target.startDate.value + " " + rec.target.startTime.value);
        formData.append("endTime", rec.target.endDate.value + " " + rec.target.endTime.value);
        formData.append("sessionLength", rec.target.sessionLength.value);

        rec.preventDefault();
        fetch(`../${this.props.userID}/courseLinkRecs`, {method: "POST", body: formData})
             .then(this.handleResponse);
        rec.target.reset(); // clear the form entries
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
        // Get course information for the autocomplete
        this.getDataFromServer();
    }

    async getDataFromServer() {
        this.setState({ courses: await (await fetch(`/${this.props.userID}/courses`)).json() });
    }

    formatTime(x) {
        let hours = x.getHours();
        let ampm = (hours >= 12 && hours < 24) ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        hours = hours < 10 ? "0" + hours : hours;
        let minutes = x.getMinutes();
        minutes = minutes < 10 ? '0' + minutes : minutes;
        return hours + ":" + minutes + " " + ampm;
    }

    formatDay(x) {
        let date = x.getFullYear() + '-' + (x.getMonth() + 1) + '-';
        date = (x.getDate() < 10) ? date + "0" + x.getDate() : date + x.getDate();
        return date;
    }

    render() {
        let style = {display: "none"};
        if (this.props.showBuddyRecForm) {
            style = {display: "block"}
        }

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
        M.Autocomplete.init(document.querySelector('.autocompleteBudRec'), options);

        let date = new Date();
        // Define default start date
        let x = date.getTime();
        let y = x + 6 * 60 * 60 * 1000;
        let X = new Date(x);
        let Y = new Date(y);
        let timeX = this.formatTime(X);
        let timeY = this.formatTime(Y);
        let dayX = this.formatDay(X);
        let dayY = this.formatDay(Y);

        return (
            <form id="eventform" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="courseNumber">Course Number (ex EN.601.226)</label>
                    <input id="courseNumber" name="courseNumber" type="text" className="autocompleteBudRec" required/>
                </div>
                <div className="input-field">
                    <label htmlFor="startDate" className="active">Recommend no earlier than this day</label>
                    <input id="startDate" type="text" className="datepicker" defaultValue={dayX} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="startTime" className="active">Recommend no earlier than this time</label>
                    <input id="startTime" name="startTime" type="text" className="timepicker" defaultValue={timeX}
                           required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endDate" className="active">Recommend no later than this day</label>
                    <input id="endDate" name="endDate" type="text" className="datepicker" defaultValue={dayY} required/>
                </div>
                <div className="input-field">
                    <label htmlFor="endTime" className="active">Recommend no later than this time</label>
                    <input id="endTime" name="endTime" type="text" className="timepicker" defaultValue={timeY}
                           required/>
                </div>
                <div className="input-field">
                    <label htmlFor="sessionLength" className="active">Study for this long (in hours) </label>
                    <input id="sessionLength" name="sessionLength" type="number" defaultValue="1" min="1" max="6"
                           required/> {/* These parameters should be put elsewhere */}
                </div>
                <button className="btn white-text">Get Recommendation!</button>
            </form>
        );
    }
}