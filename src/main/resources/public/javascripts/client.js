class Application extends React.Component {
    render() {
        return (
            <div>
            <PlusButton className="plus-at-the-top"/>
            <Header/>
            <EventList/>
            <PlusButton className="plus-at-the-bottom"/>
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
