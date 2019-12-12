class Profile extends React.Component {
    constructor(props) {
        super(props);

        // Determine the userId from the website url. (location.search)
        const parameters = location.search.substring(1).split("&");
        const temp = parameters[0].split("=");
        const id = unescape(temp[1]);
        this.state = {userId: id, showAddCourseForm: false};
    }

    flipAddCourseFormState() {
        this.setState({showAddCourseForm: !this.state.showAddCourseForm});
    }

    render() {
        return (
            <div>
                <Header userId={this.state.userId}/>
                <div className="profile-body">
                    <ProfileInfo userId={this.state.userId}/>
                    <CurrentCourses
                        userId={this.state.userId}
                        flipAddCourseFormState={this.flipAddCourseFormState.bind(this)}
                        showAddCourseForm={this.state.showAddCourseForm}
                    />
                </div>
            </div>
        );
    }
}

ReactDOM.render(<Profile/>, document.querySelector("#profile-page"));
