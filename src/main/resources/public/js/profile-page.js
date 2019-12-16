class Profile extends React.Component {
    constructor(props) {
        super(props);

        this.state = {user: null, showAddCourseForm: false, showRemoveCourseForm: false};
    }

    async getCurrentUser() {
        const user = await (await fetch("/users/current")).json();
        if (user !== 0) {
            this.setState({ user: user });
        }
    }

    componentDidMount() {
        this.getCurrentUser();
    }

    flipAddCourseFormState() {
        this.setState({showAddCourseForm: !this.state.showAddCourseForm});
    }

    flipRemoveCourseFormState() {
        this.setState({showRemoveCourseForm: !this.state.showRemoveCourseForm});
    }

    render() {
        if (this.state.user === null) {
            return null;
        }
        return (
            <div>
                <Header/>
                <div className="profile-body">
                    <div className="profileColumn center-align">
                        <ProfileInfo userId={this.state.user.id}/>
                        <CurrentCourses
                            userId={this.state.user.id}
                            flipAddCourseFormState={this.flipAddCourseFormState.bind(this)}
                            showAddCourseForm={this.state.showAddCourseForm}
                            flipRemoveCourseFormState={this.flipRemoveCourseFormState.bind(this)}
                            showRemoveCourseForm={this.state.showRemoveCourseForm}
                        />
                        <Friends userId={this.state.user.id}/>
                    </div>
                </div>
            </div>
        );
    }
}

ReactDOM.render(<Profile/>, document.querySelector("#profile-page"));
