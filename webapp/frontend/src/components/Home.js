import React from "react";
import { Container} from "react-bootstrap";
import ArticleContainer from "./ArticleContainer";
import { popular_subcategories, inventory } from "./Universals";

const Home = () => {
    return (
        <Container className="home" >
           {ArticleContainer(`${inventory}?category=headphones`, "Headphones")}
           {ArticleContainer(`${inventory}?category=washing machine`, "Washing Machines")}
        </Container>
    )

}

export default Home;