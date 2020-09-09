import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    topics: [],
    answers: [],
    score: 0
  },
  mutations: {
    setRandomTopics (state, topics) {
      state.topics = topics
      window.sessionStorage.setItem('local_topics', JSON.stringify(topics))
    },
    restoreRandomTopics (state, topics) {
      state.topics = topics
    },
    addAnswer (state, answer) {
      state.answers.push(answer)
    },
    clearAnswers (state) {
      state.answers = []
    },
    setScore (state, score) {
      state.score = score
    }
  },
  actions: {
  },
  modules: {
  }
})
