# vetlliga-back

## Deployment

```
docker build -t vetlliga-backend:latest .
docker tag vetlliga-backend:latest jonasrodriguez/vetlliga-backend:latest
docker push jonasrodriguez/vetlliga-backend:latest
```
Multiple tags

```
docker build -t jonasrodriguez/vetlliga-backend:1.5.2 -t jonasrodriguez/vetlliga-backend:latest .
docker push jonasrodriguez/vetlliga-backend:1.5.2
docker push jonasrodriguez/vetlliga-backend:latest
```
Build for ARM64

```
docker build --platform=linux/arm64 -t jonasrodriguez/vetlliga-backend:1.5.2 -t jonasrodriguez/vetlliga-backend:latest --push --no-cache .
```