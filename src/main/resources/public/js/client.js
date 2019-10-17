class Application extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showForm: false};
    }

    flipFormState() {
        this.setState({showForm: !this.state.showForm});
    }

    render() {
        return (
            <div>
                <Header/>
                <NewEventButton className="new-event-button btn white-text" flip={this.flipFormState.bind(this)} showForm={this.state.showForm}/>
                <NewEventForm showForm = {this.state.showForm} flip={this.flipFormState.bind(this)}/>
                <EventList/>

            </div>
        );
    }
}

const Header = () => (
    <header>
    <h1>StudyBuddy</h1>
    <p><small>A <a href="https://github.com/jhu-oose/2019-group-jhuoosers">Class Scheduling Application</a> for <a href="https://www.jhu-oose.com">OOSE</a></small></p>
</header>
);

ReactDOM.render(<Application/>, document.querySelector("#application"));


