FROM node

WORKDIR /cronmonitor

ARG NODE_ENV
ENV NODE_ENV $NODE_ENV

COPY package.json /cronmonitor/app/
RUN npm install

COPY . /cronmonitor/app

# replace this with your application's default port
EXPOSE 8080
CMD [ "npm", "start" ]
