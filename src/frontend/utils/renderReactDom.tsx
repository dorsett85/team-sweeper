import React from 'react';
import ReactDOM from 'react-dom';

/**
 * Convenience wrapper to render a react app inside of a dom element
 */
export const renderReactDom = (component: React.ReactElement, elementId = 'root'): void => {
  ReactDOM.render(
    <React.StrictMode>{component}</React.StrictMode>,
    document.getElementById(elementId)
  );
};
