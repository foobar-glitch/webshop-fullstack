import React, { useEffect, useState } from "react";
import { Container, Row, Col, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Rating from '@mui/material/Rating';
import useFetchGET from "./useFetchGET";
import { average_rating_endpoint, number_rating_enpoint } from "./Universals";


const ArticleShowCase = ({ articles, OpenProduct }) => {
    const [ratings, setRatings] = useState({});
  
    useEffect(() => {
      const fetchRatings = async () => {
        const results = await Promise.all(
          articles.map(async (art) => {
            try {
              const averageStarsRes = await fetch(`${average_rating_endpoint}?inventoryId=${art.inventoryId}`);
              const averageData = await averageStarsRes.json(); // e.g. { rating: 4.657 }
  
              const numberRatingRes = await fetch(`${number_rating_enpoint}?inventoryId=${art.inventoryId}`);
              const numberData = await numberRatingRes.json(); // e.g. { numRatings: 23 }
  
              const rating = parseFloat(Number(averageData).toFixed(2)) || 0;
              const count = numberData || 0;
  
              return { id: art.inventoryId, rating, count };
            } catch (err) {
              console.error(`Failed to fetch rating for ${art.inventoryId}`, err);
              return { id: art.inventoryId, rating: 0, count: 0 };
            }
          })
        );
  
        const ratingMap = results.reduce((acc, { id, rating, count }) => {
          acc[id] = [rating, count];
          return acc;
        }, {});
  
        setRatings(ratingMap);
      };
  
      fetchRatings();
    }, [articles]);
  
    return (
      <>
        {articles.map((art) => {
          const [rating, count] = ratings[art.inventoryId] || [0, 0];
  
          return (
            <Col
              key={art.inventoryId}
              xs={2}
              md={2}
              lg={2}
              className="article-column"
              onClick={() => OpenProduct(art.inventoryId)}
            >
              <Row className="preview-picture">
                {/* Optional product image */}
              </Row>
  
              <Row className="product">
                {art.productName}
              </Row>
  
              <Row className="rating">
                <Rating
                  className="rating-stars"
                  name="read-only"
                  precision={0.1}
                  value={rating}
                  readOnly
                />
                <div className="rating-value">
                  {rating ? `${rating}/5` : "0"} ({count})
                </div>
              </Row>
  
              <Row className="price">
                €{art.price}
              </Row>
            </Col>
          );
        })}
      </>
    );
  };

function getSubarray(array, start, length){
    if(start+length>= array.length){
        return array.slice(start, array.length)
    }
    return array.slice(start, start+length)
}


const ArticleContainer = (article_endpoint, category)  => {
    const [startSlide, setStartSlide] = useState(0);

    // length how many articles shown
    const length = 4;
    const { data, isPending, error }= useFetchGET(article_endpoint)
    

    const navigate = useNavigate();

    const moveRight = (maxLength) => {
        if(startSlide+length < maxLength){
            setStartSlide(startSlide+length)
        }
        console.log(startSlide)
    }

    const moveLeft = () => {
        if(startSlide-length<=0){
            setStartSlide(0)
        }else{
            setStartSlide(startSlide-length)
        }
        console.log(startSlide)
    }

    const OpenProduct = (product_id) => {
        console.log(product_id)
        navigate(`/entry?inventoryId=${product_id}`);
    }
    console.log(data)
    console.log(error)
    
    return (data && !isPending && !error&& <>
     <Container className="article-container">
        <Row className="article-header">
            <Col className="category">
                <h3><b>{category}</b></h3>
            </Col>
            <Col className="slide">
                <Button style={{marginRight: "5px"}} onClick={moveLeft}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-left-square" viewBox="0 0 16 16">
                        <path fill-rule="evenodd" d="M15 2a1 1 0 0 0-1-1H2a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1zM0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm11.5 5.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5z"/>
                    </svg>
                </Button>
                <Button onClick={() => moveRight(data.length)}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-right-square" viewBox="0 0 16 16">
                        <path fill-rule="evenodd" d="M15 2a1 1 0 0 0-1-1H2a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1zM0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm4.5 5.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5z"/>
                    </svg>
                </Button>
            </Col>
        </Row>
        <Row className="articles">
        <ArticleShowCase
            articles={getSubarray(Object.values(data), startSlide, length)}
            OpenProduct={OpenProduct}
        />
        </Row>
    </Container>
    </>
    );
}
export default ArticleContainer;