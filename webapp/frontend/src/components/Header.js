import React, { useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css"

import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import Offcanvas from 'react-bootstrap/Offcanvas';
import Col from "react-bootstrap/esm/Col";
import {Row} from "react-bootstrap"
import { useNavigate } from "react-router-dom";
import Modal from 'react-bootstrap/Modal';
import { api_endpoint } from "./Universals";

function SideBar() {
  const expand = 'xs'
  return (
    <>
      {
        <Navbar key={expand} expand={expand}>
          <Container className="cat-container">
            <Navbar.Toggle aria-controls={`offcanvasNavbar-expand-${expand}`} />
            <Navbar.Offcanvas
              id={`offcanvasNavbar-expand-${expand}`}
              aria-labelledby={`offcanvasNavbarLabel-expand-${expand}`}
              placement="start"
            >
              <Offcanvas.Header closeButton>
                <Offcanvas.Title id={`offcanvasNavbarLabel-expand-${expand}`}>
                  Offcanvas
                </Offcanvas.Title>
              </Offcanvas.Header>
              <Offcanvas.Body>
                <Nav className="justify-content-end flex-grow-1 pe-3">
                  <Nav.Link href="#action1">Home</Nav.Link>
                  <Nav.Link href="#action2">Link</Nav.Link>
                  <NavDropdown
                    title="Dropdown"
                    id={`offcanvasNavbarDropdown-expand-${expand}`}
                  >
                    <NavDropdown.Item href="#action3">Action</NavDropdown.Item>
                    <NavDropdown.Item href="#action4">
                      Another action
                    </NavDropdown.Item>
                    <NavDropdown.Divider />
                    <NavDropdown.Item href="#action5">
                      Something else here
                    </NavDropdown.Item>
                  </NavDropdown>
                </Nav>

              </Offcanvas.Body>
            </Navbar.Offcanvas>
          </Container>
        </Navbar>
      }
    </>
  );
}


const Header = () => {
  
  const [show, setShow] = useState()
  
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);


  const SignInUp = () => {
    return (
      <>
        <Modal className="login-form" show={show} onHide={handleClose}>
          <Modal.Header closeButton>
            <Modal.Title>Sign In</Modal.Title>
          </Modal.Header>
          <Modal.Body>
          <Form action={api_endpoint+"/login"} method="POST">
            <Form.Group className="mb-3" controlId="formBasicEmail">
              <Form.Control type="text" name="username" placeholder="username" required />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicPassword">
              <Form.Control type="password" name="password" placeholder="password" required />
              <Form.Text className="text-muted">
                Never share your password with anyone.
              </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicCheckbox">
              <Form.Check type="checkbox" name="remember" label="Check me out" />
            </Form.Group>

            <Button variant="primary" type="submit">
              Submit
            </Button>
          </Form>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
              Close
            </Button>
          </Modal.Footer>
        </Modal>
      </>
    );
  }

    
    const history = useNavigate();

    const clickWishList = () => {
      console.log(1)
      history('/wishlist')
    }

    const clickCart = () => {
      history('/cart')
    }

    const openIndex = () => {
      history('/')
    }

    /* Contains the search bar, categories, userdata */
    return (
        // The header needed for the user to navigate
        <Container fluid className="user-header">
            <Row className="g-11 mb-0">
              <Col className="shop-logo"  xs={2} md={2} lg={1}>
                <img className="logo-image" src="/images/home.svg" alt="LOGO.png" onClick={openIndex} />
              </Col>
              <Col className="navbar d-inline-flex" xs={6} md={5} lg={4}>
                <Row className="navbar-row d-flex align-items-center">
                  <Col className="categories justify-content-end">
                      {SideBar()}
                  </Col>
                  <Col className="search-bar">  
                      <Form className="search-field d-flex" onSubmit={ console.log(1) }>
                          <Form.Control
                              type="search"
                              placeholder="Search"
                              className="me-2"
                              aria-label="Search"
                          />
                          <Button variant="outline-success" type="submit">
                              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
                                  <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
                              </svg>
                          </Button>
                      </Form>
                  </Col>
                </Row>
              </Col>
              
              <Col className="icons d-inline-flex justify-content-end" xs={3} md={3} lg={3}>
                <Row className="icons-row d-flex align-items-center">
                  
                  <Col className="profile p-2" onClick={handleShow}>
                    <img className="signInUp-image" src="/images/profile-icon.svg" alt="Profile.img" />
                  </Col>
                  
                  <Col className="cart p-2" onClick={clickCart}>
                    <img className="cart-image" src="/images/shopping-cart-icon.svg" alt="Cart.img" />
                  </Col>
                </Row>
              </Col>
              
            </Row>
            {SignInUp()}
        </Container>
        
    )

}

export default Header;