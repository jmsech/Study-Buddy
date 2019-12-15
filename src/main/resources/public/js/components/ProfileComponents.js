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
                <h3>{userName}</h3>
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
                <h4>Your classes</h4><br/>
                <CollapsibleCourseList userId={this.props.userId}/>
                <div className="content-row">
                <div className="column">
                <AddCourse flipAddCourse={this.props.flipAddCourseFormState} showAddCourseForm={this.props.showAddCourseForm} userId={this.props.userId}/>
                </div>
                <div className="column">
                <RemoveCourse flipRemoveCourse={this.props.flipRemoveCourseFormState} showRemoveCourseForm={this.props.showRemoveCourseForm} userId={this.props.userId}/>
                </div>
                </div>
            <hr/>
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


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// BELOW THIS LINE IS CODE THAT BRANDON JUST ADDED ////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


class Friends extends React.Component {
    constructor(props) {
        super(props);
        this.state = {userId: this.props.userId, showAddFriendForm: false, showRemoveFriendForm: false}
    }

    flipAddFriendFormState() {
        this.setState({showAddFriendForm: !this.state.showAddFriendForm});
    }

    flipRemoveFriendFormState() {
        this.setState({showRemoveFriendForm: !this.state.showRemoveFriendForm});
    }

    render() {
        return (
            <div>
                <FriendList userId={this.props.userId}/>
                <div className="content-row">
                    <div className="column">
                    <FriendAddButton className="btn cyan darken-3 centralized-button"
                                 userId={this.props.userId}
                                 flipAddFriendFormState={this.flipAddFriendFormState.bind(this)}
                                 showAddFriendForm={this.state.showAddFriendForm}/>
                <FriendAddForm userId={this.props.userId}
                               flipAddFriendFormState={this.flipAddFriendFormState.bind(this)}
                               showAddFriendForm={this.state.showAddFriendForm}/>
                    </div>
                    <div className="column">
                <FriendRemoveButton className="btn cyan darken-3 centralized-button"
                                    userId={this.props.userId}
                                    flipRemoveFriendFormState={this.flipRemoveFriendFormState.bind(this)}
                                    showRemoveFriendForm={this.state.showRemoveFriendForm}/>
                <FriendRemoveForm userId={this.props.userId}
                                  flipRemoveFriendFormState={this.flipRemoveFriendFormState.bind(this)}
                                  showRemoveFriendForm={this.state.showRemoveFriendForm}/>
                    </div>
                    <div className="column">
                <Pendings userId={this.props.userId}/>

                <Awaitings userId={this.props.userId}/>
                    </div>
                </div>
            </div>
        )
    }
}

class FriendAddButton extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let title = "Add Friend!";
        if (this.props.showAddFriendForm) {
            title = "Cancel";
        }
        return (
            <button className="btn centralized-button"
                       onClick={() => { this.props.flipAddFriendFormState() }}
            >{title}</button>
        )
    }
}

class FriendAddForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = { users: [] };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    async componentDidMount() {
        // Get course information for the autocomplete
        this.getDataFromServer();
    }

    async getDataFromServer() {
        this.setState({ users: await (await fetch("/users/", {method : "GET"})).json() });
    }

    async handleResponse(response) {
        return response;
    }

    async handleSubmit(form) {
        form.preventDefault();
        this.props.flipAddFriendFormState();
        const formData = new FormData();
        formData.append("userId", this.props.userId);
        let name = form.target.buddyId.value;
        name = name.substr(name.indexOf("("), name.length);
        formData.append("buddyId", name);
        fetch(`../${this.props.userId}/friends/`, {method: "POST", body: formData})
            .then(this.handleResponse);
        form.target.reset(); // clear the form entries
    }

    render() {
        let users = {};
        for (let i = 0; i < this.state.users.length; i++) {
            const user = this.state.users[i];
            const name = user.name;
            const email = user.email;
            const string = name + " (" + email + ")";
            Object.assign(users, {[string]: null});
        }
        const options = {data: users, limit: 20};

        // Initialize materialize autocomplete
        M.Autocomplete.init(document.querySelectorAll('.autocompleteAddFriend'), options);

        let style = {display: "none"};
        if (this.props.showAddFriendForm) { style = {display: "block"} }

        return (
            <form id="addFriendForm" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="buddyId">User Name or Email</label>
                    <input id="buddyId" name="buddyId" type="text" className="autocompleteAddFriend" required/>
                </div>
                <button className="btn white-text">Add Friend!</button>
            </form>
        );
    }
}

class FriendRemoveButton extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let title = "Remove Friend :(";
        if (this.props.showRemoveFriendForm) {
            title = "Cancel";
        }
        return (
            <button className="btn centralized-button"
                    onClick={() => { this.props.flipRemoveFriendFormState() }}
            >{title}</button>
        )
    }
}

class FriendRemoveForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = { users: [] };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleResponse = this.handleResponse.bind(this);
    }

    async componentDidMount() {
        // Get course information for the autocomplete
        this.getDataFromServer();
    }

    async getDataFromServer() {
        this.setState({ users: await (await fetch(`/${this.props.userId}/friends/`, {method: "GET"})).json() });
    }

    async handleResponse(response) {
        return response;
    }

    async handleSubmit(form) {
        form.preventDefault();
        this.props.flipRemoveFriendFormState();
        const formData = new FormData();
        formData.append("userId", this.props.userId);
        let name = form.target.buddyId.value;
        name = name.substr(name.indexOf("("), name.length);
        formData.append("buddyId", name);
        fetch(`../${this.props.userId}/friends/`, {method: "PUT", body: formData})
            .then(this.handleResponse);
        form.target.reset(); // clear the form entries
    }

    render() {
        let users = {};
        for (let i = 0; i < this.state.users.length; i++) {
            const user = this.state.users[i];
            const name = user.name;
            const email = user.email;
            const string = name + " (" + email + ")";
            Object.assign(users, {[string]: null});
        }
        const options = {data: users, limit: 20};

        // Initialize materialize autocomplete
        M.Autocomplete.init(document.querySelectorAll('.autocompleteRemoveFriend'), options);

        let style = {display: "none"};
        if (this.props.showRemoveFriendForm) { style = {display: "block"} }

        return (
            <form id="addFriendForm" onSubmit={this.handleSubmit} style={style}>
                <div className="input-field">
                    <label htmlFor="buddyId">User Name or Email</label>
                    <input id="buddyId" name="buddyId" type="text" className="autocompleteRemoveFriend" required/>
                </div>
                <button className="btn white-text">Remove Friend!</button>
            </form>
        );
    }
}

class FriendList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {users: []}
    }

    async getDataFromServer() {
        this.setState({ users: await (await fetch(`/${this.props.userId}/friends/`, {method: "GET"})).json() });
        window.setTimeout(() => {this.getDataFromServer();}, 200);
    }

    componentDidMount() {
        this.getDataFromServer();
        // Initialize materialize collapsible
        // M.Collapsible.init(document.querySelectorAll('.collapsible'), {});
    }

    render () {
        return (
            <div>
                <h4 className="center">Your Friends</h4>
                <ul className ="friendslist">
                    {this.state.users.map(user => <Friend key={user.userId} user={user}/>)}
                </ul>
            </div>
        )
    }
}

class Friend extends React.Component {
    constructor(props) {
        super(props);
    }

    render () {
        return (
            <div>
                <p>{this.props.user.name}</p>
            </div>
        )
    }
}

class Pendings extends React.Component {
    constructor(props) {
        super(props);
        this.state = {userId: this.props.userId, showPending: false, numEntries: 0}
    }

    flipPendingState() {
        this.setState({showPending: !this.state.showPending});
    }

    changeNumEntries(num) {
        this.setState({numEntries: num})
    }

    render () {
        return (
            <div>
                <PendingButton flipPendingState={this.flipPendingState.bind(this)}
                               numEntries={this.state.numEntries}
                               showPending={this.state.showPending}/>
                <PendingList userId={this.props.userId}
                             showPending={this.state.showPending}
                             changeNumEntries={this.changeNumEntries.bind(this)}/>
            </div>
        )
    }
}

class PendingButton extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let title = "See Pending (" + this.props.numEntries + ")";
        if (this.props.showPending) {
            title = "Hide Pending (" + this.props.numEntries + ")";
        }
        return (
            <button className="btn centralized-button"
                    onClick={() => { this.props.flipPendingState() }}
            >{title}</button>
        )
    }
}

class PendingList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {users: []}
    }

    async getDataFromServer() {
        this.setState({ users: await (await fetch(`/${this.props.userId}/followers/`, {method: "GET"})).json() });
        this.props.changeNumEntries(this.state.users.length);
        window.setTimeout(() => {this.getDataFromServer();}, 200);
    }

    componentDidMount() {
        this.getDataFromServer();
        // Initialize materialize collapsible
        // M.Collapsible.init(document.querySelectorAll('.collapsible'), {});
    }

    render () {
        let style = {display : "none"};
        if (this.props.showPending) {style = {display : "block"}; }
        return (
            <div style={style}>
                <ul>
                    {this.state.users.map(user => <Pending key={user.userId} user={user} userId={this.props.userId}/>)}
                </ul>
            </div>
        )
    }
}

class Pending extends React.Component {
    constructor(props) {
        super(props);
    }

    async handleResponse(response) {
        return response;
    }

    remove() {
        const formData = new FormData();
        formData.append("userId", this.props.userId);
        formData.append("buddyId", "(" + this.props.user.email + ")");
        fetch(`../${this.props.userId}/friends/`, {method: "PUT", body: formData})
            .then(this.handleResponse);
    }

    add() {
        const formData = new FormData();
        formData.append("userId", this.props.userId);
        formData.append("buddyId", "(" + this.props.user.email + ")");
        fetch(`../${this.props.userId}/friends/`, {method: "POST", body: formData})
            .then(this.handleResponse);
    }

    render () {
        return (
            <div>
                <div className="content-row">
                    <div className="column">
                        <div className = "card-content">
                            <p>{this.props.user.name}</p>
                        </div>
                    </div>
                    <div className="column">
                <button onClick={() => { this.remove() }} className="btn white-text">-</button>
                <button onClick={() => { this.add() }} className="btn white-text">+</button>
                    </div>
                </div>
            </div>
        )
    }
}

class Awaitings extends React.Component {
    constructor(props) {
        super(props);
        this.state = {userId: this.props.userId, showAwaiting: false, numEntries: 0}
    }

    flipAwaitingState() {
        this.setState({showAwaiting: !this.state.showAwaiting});
    }

    changeNumEntries(num) {
        this.setState({numEntries: num})
    }

    render () {
        return (
            <div>
                <AwaitingButton flipAwaitingState={this.flipAwaitingState.bind(this)}
                                numEntries={this.state.numEntries}
                                showAwaiting={this.state.showAwaiting}/>
                <AwaitingList userId={this.props.userId}
                             showAwaiting={this.state.showAwaiting}
                             changeNumEntries={this.changeNumEntries.bind(this)}/>
            </div>
        )
    }
}

class AwaitingButton extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let title = "See Awaiting (" + this.props.numEntries + ")";
        if (this.props.showAwaiting) {
            title = "Hide Awaiting (" + this.props.numEntries + ")";
        }
        return (
            <button className="btn centralized-button"
                    onClick={() => { this.props.flipAwaitingState() }}
            >{title}</button>
        )
    }
}

class AwaitingList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {users: []}
    }

    async getDataFromServer() {
        this.setState({ users: await (await fetch(`/${this.props.userId}/followers/`, {method: "POST"})).json() });
        this.props.changeNumEntries(this.state.users.length)
        window.setTimeout(() => {this.getDataFromServer();}, 200);
    }

    componentDidMount() {
        this.getDataFromServer();
        // Initialize materialize collapsible
        // M.Collapsible.init(document.querySelectorAll('.collapsible'), {});
    }

    render () {
        let style = {display : "none"};
        if (this.props.showAwaiting) {style = {display : "block"}; }
        return (
            <div style={style}>
                <ul>
                    {this.state.users.map(user => <Awaiting key={user.userId} user={user}/>)}
                </ul>
            </div>
        )
    }
}

class Awaiting extends React.Component {
    constructor(props) {
        super(props);
    }

    render () {
        return (
            <div>
                <p>{this.props.user.name}</p>
            </div>
        )
    }
}
