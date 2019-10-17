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
                <NewEventButton className="new-event-button btn" flip={this.flipFormState.bind(this)} showForm={this.state.showForm}/>
                <User showForm = {this.state.showForm} flip={this.flipFormState.bind(this)}/>

            </div>
        );
    }
}

const Header = () => (
    <header>
    <h1>StudyBuddy</h1>
    <p><small>A <a href="https://github.com/jhu-oose/2019-group-jhuoosers">Study Scheduling Application</a> for <a href="https://www.jhu-oose.com">OOSE</a></small></p>
</header>
);

ReactDOM.render(<Application/>, document.querySelector("#application"));


