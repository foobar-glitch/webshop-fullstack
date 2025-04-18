import React from "react";
import NotFound from "./NotFound";

const checkAuthentication = (component, condition) => {
    return condition? React.createElement(component): React.createElement(NotFound)
}

const ProtectedRoute = ({ component, condition }) => {
    return <>
        {checkAuthentication(component, condition)}
    </>
};

export default ProtectedRoute;