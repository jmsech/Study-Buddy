// class SeeCourses extends React.Component {
//     render() {
//         let text = "See Courses";
//         return (
//             <div>
//                 <button data-target="slide-out" className="btn sidenav-trigger"> {text} </button>
//                 <CourseList userID = {this.props.userID}/>
//             </div>
//         )
//     }
// }
//
// class CourseList extends React.Component {
//     constructor(props) {
//         super(props);
//         this.state = {
//             courses: []
//         };
//     }
//
//     async getDataFromServer() {
//         this.setState({courses: await (await fetch(`/${this.props.userID}/courses`)).json()});
//         window.setTimeout(() => {
//             this.getDataFromServer();
//         }, 200);
//     }
//
//     async componentDidMount() {
//         await this.getDataFromServer();
//         M.Sidenav.init(document.querySelectorAll('.sidenav'), {
//         });
//     }
//
//     render() {
//         return (
//             <div>
//                 <ul id="slide-out" className="sidenav teal lighten-1">
//                     <span className="card-title">
//                         <h4>Your Courses</h4>
//                     </span>
//                     {this.state.courses.map(course =>
//                         <Course key={course.id}
//                                 course={course}
//                                 userID={this.props.userID}
//                         />
//                     )}
//                 </ul>
//             </div>
//         );
//     }
// }
// class Course extends React.Component {
//     constructor(props) {
//         super(props);
//         this.state = {showDeadlineForm: false}
//     }
//
//     flipDeadlineFormState() {
//         this.setState({showDeadlineForm: !this.state.showDeadlineForm} );
//     }
//
//     render() {
//         return (
//             <div className="card hoverable teal lighten-2">
//                 <div className="card-content black-text">
//                     <span className="card-title">
//                         {/*<CourseHeader course={this.props.course}/>*/}
//                         <h6>{this.props.course.courseName}</h6>
//                     </span>
//                     <p>{this.props.course.courseNumber + " (" + this.props.course.section + ") "}</p>
//                     {/*<p>{this.props.course.courseDescription}</p> //FIXME: This is very long*/}
//                     <p>{this.props.course.instructor}</p>
//                     <p>{this.props.course.timeString}</p>
//                     <CourseLocation course={this.props.course}/>
//                     <DeadlineButton userID={this.props.userID} active={this.state.showDeadlineForm} flip = {this.flipDeadlineFormState.bind(this)}/>
//                     <NewDeadlineForm active={this.state.showDeadlineForm} flip = {this.flipDeadlineFormState.bind(this)} courseID={this.props.course.id}/>
//                 </div>
//             </div>
//         );
//     }
// }
// class CourseLocation extends React.Component {
//     constructor(props) {
//         super(props);
//         this.state = null;
//     }
//
//     render() {
//         if (this.props.course.location !== "" && this.props.course.description !== null){
//             return (
//                 <div>
//                     <p><i className="tiny material-icons">location_on</i>{this.props.course.location}</p>
//                 </div>
//             );
//         } else return null;
//     }
// }
//
// class DeadlineButton extends React.Component {
//     render() {
//         let title = "Add Deadline";
//         if (this.props.active) {
//             title = "Cancel";
//         }
//         return <button className="btn centralized-button" onClick={() => { this.props.flip() }}>{title}</button>;
//     }
// }
//
// class NewDeadlineForm extends React.Component {
//     constructor(props) {
//         super(props);
//         this.state = {value: ''};
//
//         this.handleChange = this.handleChange.bind(this);
//         this.handleSubmit = this.handleSubmit.bind(this);
//     }
//
//     handleChange(deadline) {
//         this.setState({value: deadline.target.value});
//     }
//
//     handleSubmit(deadline) {
//         this.props.flip();
//         const formData = new FormData();
//         formData.append("userID", this.props.userID);
//         formData.append("title", deadline.target.title.value);
//         // combine tim/date into the format yyyy-mm-dd 00:00
//         formData.append("dueDate", deadline.target.startDate.value + " " + deadline.target.dueTime.value);
//         formData.append("description", deadline.target.description.value);
//         formData.append("courseID", this.props.courseID);
//         deadline.preventDefault();
//         // TODO: Call appropriate path on Server
//         console.log(this.props.userID)
//         fetch(`../${this.props.userID}/deadline`, {method: "POST", body: formData});
//         deadline.target.reset(); // clear the form entries
//     }
//
//     componentDidMount() {
//         M.Datepicker.init(document.querySelectorAll('.datepicker'), {
//             autoClose: true,
//             showClearBtn: true,
//             format: "yyyy-mm-dd",
//             minDate: (new Date())
//         });
//         M.Timepicker.init(document.querySelectorAll('.timepicker'), {
//             showClearBtn: true
//         });
//     }
//
//     formatAMPM(hours, minutes) {
//         var ampm = (hours >= 12 && hours < 24) ? 'PM' : 'AM';
//         hours = hours % 12;
//         hours = hours ? hours : 12; // the hour '0' should be '12'
//         hours = hours < 10 ? "0" + hours : hours;
//         minutes = minutes < 10 ? '0'+ minutes : minutes;
//         return hours + ':' + minutes + ' ' + ampm;
//     }
//
//     render() {
//         let style = {display: "none"};
//         if (this.props.active) { style = {display: "block"}};
//         let date = new Date();
//         // Define default start date
//         let defaultStartDate = date.getFullYear()+'-'+(date.getMonth()+1)+'-';
//         if (date.getDate() < 10) {
//             defaultStartDate = defaultStartDate + "0" + date.getDate();
//         } else {
//             defaultStartDate = defaultStartDate + date.getDate();
//         }
//         return (
//             <form id="eventform" onSubmit={this.handleSubmit} style={style}>
//                 <div className="input-field">
//                     <label htmlFor="title">Title</label>
//                     <input id="title" name="title" type="text" required/>
//                 </div>
//                 <div className="input-field">
//                     <label htmlFor="description">Description</label>
//                     <textarea id="description" name="description" className="materialize-textarea"/>
//                 </div>
//                 <div className="input-field">
//                     <label htmlFor="startDate" className="active">Due Date</label>
//                     <input id="startDate" type="text" className="datepicker" defaultValue={defaultStartDate} required/>
//                 </div>
//                 <div className="input-field">
//                     <label htmlFor="startTime" className="active">Due Time</label>
//                     <input id="startTime" name="startTime" type="text" className="timepicker"/>
//                 </div>
//                 <button className="btn white-text">Save Deadline</button>
//             </form>
//         );
//     }
// }
//
//
//
