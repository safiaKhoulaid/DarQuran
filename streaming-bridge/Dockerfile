FROM node:20-alpine

RUN apk add --no-cache ffmpeg

# Évite d’utiliser le binaire glibc de ffmpeg-static sous Alpine (mieux : ffmpeg apk)
ENV FFMPEG_PATH=/usr/bin/ffmpeg

WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY server.js ./

EXPOSE 9090
CMD ["node", "server.js"]
