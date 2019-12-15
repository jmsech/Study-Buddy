class Application extends React.Component {
    constructor(props) {
        super(props);

        this.state = {showEventForm: false, showRecForm: false, showCourseDisplay: false, user: null,
            showBuddyRecForm: false, showAddCourseForm: false};
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

    flipEventFormState() {
        this.setState({showEventForm: !this.state.showEventForm});
    }

    flipCourseDisplay() {
        this.setState({showCourseDisplay: !this.state.showCourseDisplay});
    }

    flipRecFormState() {
        this.setState({showRecForm: !this.state.showRecForm});
    }

    flipBuddyRecFormState() {
        this.setState({showBuddyRecForm: !this.state.showBuddyRecForm});
    }

    flipAddCourseFormState() {
        this.setState({showAddCourseForm: !this.state.showAddCourseForm});
    }

    render() {
        if (this.state.user === null) {
            return (<LoginPage/>);
        }
        return (
            <div>
                <div>
                    <Header/>
                    <div className="centralized-body">
                        <User showCourseDisplay = {this.state.showCourseDisplay}
                              flipCourseDisplay = {this.flipCourseDisplay.bind(this)}
                              showEventForm = {this.state.showEventForm}
                              flipEventFormState={this.flipEventFormState.bind(this)}
                              showRecForm={this.state.showRecForm}
                              flipRecFormState={this.flipRecFormState.bind(this)}
                              showBuddyRecForm={this.state.showBuddyRecForm}
                              flipBuddyRecFormState={this.flipBuddyRecFormState.bind(this)}
                              showAddCourseForm={this.state.showAddCourseForm}
                              flipAddCourseFormState={this.flipAddCourseFormState.bind(this)}
                              user={this.state.user}/>
                    </div>
                </div>
            </div>
        );
    }
}

ReactDOM.render(<Application/>, document.querySelector("#index"));


