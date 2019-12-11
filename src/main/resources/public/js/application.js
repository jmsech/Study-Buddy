class Application extends React.Component {
    constructor(props) {
        super(props);

        // Determine the userId from the website url. (location.search)
        const parameters = location.search.substring(1).split("&");
        const temp = parameters[0].split("=");
        const id = unescape(temp[1]);

        this.state = {showEventForm: false, showRecForm: false, showCourseDisplay: false, userId: id}
    }

    flipEventFormState() {
        this.setState({showEventForm: !this.state.showEventForm});
    }

    flipRecFormState() {
        this.setState({showRecForm: !this.state.showRecForm});
    }

    flipCourseDisplay() {
        this.setState({showCourseDisplay: !this.state.showCourseDisplay});
    }

    render() {
        return (
            <div>
                <div>
                    <Header userId={this.state.userId}/>
                    <div className="centralized-body">
                        <User showCourseDisplay = {this.state.showCourseDisplay}
                              flipCourseDisplay = {this.flipCourseDisplay.bind(this)}
                              showEventForm = {this.state.showEventForm}
                              flipEventFormState={this.flipEventFormState.bind(this)}
                              showRecForm={this.state.showRecForm}
                              flipRecFormState={this.flipRecFormState.bind(this)}
                              userId={this.state.userId}/>
                    </div>
                </div>
            </div>
        );
    }
}

ReactDOM.render(<Application/>, document.querySelector("#application"));


