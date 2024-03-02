**To run application**
- Run the Dockerfile
- temporal server start-dev
- ./gradlew bootRun

**To test APIs**
- Update stripe.api-key value in application.properties to your Stripe instance's secret key
- Go to http://127.0.0.1:8080/swagger-ui/index.html#/ in your browser. Click on API to test, add required data and hit "Execute" (added swagger for testing)
- Go to http://127.0.0.1:8233/ in your browser. This is the temporal UI page to see workflow execution status.
